/*
 * Created 2018-9-12 15:23:45
 */
package cn.com.yting.kxy.web.school;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyRecord;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.resource.CurrencyToConsumables;
import cn.com.yting.kxy.web.player.ParameterSpaceProvider;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.quest.QuestRecord;
import cn.com.yting.kxy.web.quest.QuestRepository;
import cn.com.yting.kxy.web.quest.model.QuestStatus;
import cn.com.yting.kxy.web.school.resource.SchoolAbilityConsumption;
import cn.com.yting.kxy.web.school.resource.SchoolInformation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class SchoolService implements InitializingBean, ParameterSpaceProvider {

    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private QuestRepository questRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ResourceContext resourceContext;

    private int maxAbilityLevel;

    @Override
    public void afterPropertiesSet() throws Exception {
        maxAbilityLevel = resourceContext.getLoader(SchoolAbilityConsumption.class).getAll().keySet().stream()
                .mapToInt(Long::intValue)
                .max().orElse(0);
    }

    public SchoolRecord create(long accountId, long schoolId) {
        if (schoolRepository.existsById(accountId)) {
            throw KxyWebException.unknown("已经存在门派记录");
        }
        if (!resourceContext.getLoader(SchoolInformation.class).exists(schoolId)) {
            throw KxyWebException.unknown("门派不存在，schoolId=" + schoolId);
        }
        QuestRecord questRecord = questRepository.findById(accountId, SchoolConstants.PREREQUIREMENT_QUEST_ID).orElse(null);
        if (questRecord == null || !questRecord.getQuestStatus().equals(QuestStatus.COMPLETED)) {
            throw KxyWebException.unknown("前置任务未完成");
        }

        SchoolRecord record = new SchoolRecord();
        record.setAccountId(accountId);
        record.setSchoolId(schoolId);
        return schoolRepository.saveAndFlush(record);
    }

    public SchoolRecord levelup(long accountId, int abilityIndex) {
        SchoolRecord record = schoolRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.notFound("门派记录不存在"));
        Player player = playerRepository.findById(accountId).get();
        int currentAbilityLevel = record.getAblitiesLevelList().get(abilityIndex);
        if (currentAbilityLevel >= maxAbilityLevel || currentAbilityLevel >= getAbilityLevelUpperLimit(player) + record.getExtra_ability_level_limit()) {
            throw SchoolException.levelReachLimit();
        }
        SchoolAbilityConsumption schoolAbilityConsumption = resourceContext.getLoader(SchoolAbilityConsumption.class).get(currentAbilityLevel);
        CurrencyRecord currencyRecord = currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_门贡);
        if (currencyRecord.getAmount() < schoolAbilityConsumption.getContribution()) {
            throw SchoolException.insufficientCurrency();
        }

        record.getAblitiesLevelList().set(abilityIndex, currentAbilityLevel + 1);
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_门贡, schoolAbilityConsumption.getContribution());
        return record;
    }

    public SchoolRecord levelupAMAP(long accountId) {
        SchoolRecord record = schoolRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.notFound("门派记录不存在"));
        Player player = playerRepository.findById(accountId).get();
        CurrencyRecord currencyRecord = currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_门贡);
        long cost = 0;
        int levelUpperLimit = Math.min(maxAbilityLevel, getAbilityLevelUpperLimit(player) + record.getExtra_ability_level_limit());

        @AllArgsConstructor
        class IndexedLevel {

            int index;
            int level;
        }
        List<Integer> ablitiesLevelList = record.getAblitiesLevelList();
        IndexedLevel[] indexedLevelArr = new IndexedLevel[ablitiesLevelList.size()];
        for (int i = 0; i < ablitiesLevelList.size(); i++) {
            indexedLevelArr[i] = new IndexedLevel(i, ablitiesLevelList.get(i));
        }
        assert indexedLevelArr.length >= 2;

        Comparator<IndexedLevel> cmp = Comparator.<IndexedLevel>comparingInt(it -> it.level).thenComparing(it -> it.index);
        mainLoop:
        for (int safeCount = 0; safeCount < 300; safeCount++) {
            // 小数组不管怎么样排序都很快
            Arrays.sort(indexedLevelArr, cmp);
            while (cmp.compare(indexedLevelArr[0], indexedLevelArr[1]) < 0) {
                if (indexedLevelArr[0].level >= levelUpperLimit) {
                    break mainLoop;
                }
                SchoolAbilityConsumption schoolAbilityConsumption = resourceContext.getLoader(SchoolAbilityConsumption.class).get(indexedLevelArr[0].level);
                if (cost + schoolAbilityConsumption.getContribution() > currencyRecord.getAmount()) {
                    break mainLoop;
                }
                cost += schoolAbilityConsumption.getContribution();
                indexedLevelArr[0].level++;
            }
        }

        for (int i = 0; i < indexedLevelArr.length; i++) {
            ablitiesLevelList.set(indexedLevelArr[i].index, indexedLevelArr[i].level);
        }
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_门贡, cost);
        return record;
    }

    public SchoolRecord redeemExtraAbilityLevelLimit(long accountId, long currencyId) {
        Long limit = resourceContext.getLoader(CurrencyToConsumables.class).getAll().values().stream()
                .filter(it -> it.getEffectID() == 8)
                .filter(it -> it.getId() == currencyId)
                .map(it -> it.getEffectParameter())
                .findAny().orElse(null);
        if (limit == null) {
            throw KxyWebException.unknown("不能用指定的货币兑换奖励");
        }
        //
        SchoolRecord record = schoolRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.notFound("门派记录不存在"));
        if (record.getExtra_ability_level_limit() + 1 > SchoolConstants.MAX_EXTRA_ABILITY_LEVEL_LIMIT) {
            throw SchoolException.levelReachLimit();
        }
        currencyService.decreaseCurrency(accountId, currencyId, 1);
        record.setExtra_ability_level_limit(record.getExtra_ability_level_limit() + 1);
        return record;
    }

    public SchoolRecord redeemChangeSchool(long accountId, long currencyId, long schoolId) {
        Long limit = resourceContext.getLoader(CurrencyToConsumables.class).getAll().values().stream()
                .filter(it -> it.getEffectID() == 9)
                .filter(it -> it.getId() == currencyId)
                .map(it -> it.getEffectParameter())
                .findAny().orElse(null);
        if (limit == null) {
            throw KxyWebException.unknown("不能用指定的货币兑换奖励");
        }
        if (!resourceContext.getLoader(SchoolInformation.class).exists(schoolId)) {
            throw KxyWebException.unknown("门派不存在，schoolId=" + schoolId);
        }
        //
        SchoolRecord record = schoolRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.notFound("门派记录不存在"));
        if (record.getSchoolId() == schoolId) {
            throw KxyWebException.unknown("当前门派与目标门派一致");
        }
        //
        currencyService.decreaseCurrency(accountId, currencyId, 1);
        long prevQuestId = 0, afterQuestId = 0;
        switch ((int) record.getSchoolId()) {
            case 101: {
                prevQuestId = 700028;
                break;
            }
            case 102: {
                prevQuestId = 700029;
                break;
            }
            case 103: {
                prevQuestId = 700030;
                break;
            }
            case 104: {
                prevQuestId = 700031;
                break;
            }
        }
        switch ((int) schoolId) {
            case 101: {
                afterQuestId = 700028;
                break;
            }
            case 102: {
                afterQuestId = 700029;
                break;
            }
            case 103: {
                afterQuestId = 700030;
                break;
            }
            case 104: {
                afterQuestId = 700031;
                break;
            }
        }
        QuestRecord questRecord = questRepository.findById(accountId, prevQuestId).orElse(null);
        QuestRecord newRecord = new QuestRecord();
        newRecord.setAccountId(accountId);
        newRecord.setQuestId(afterQuestId);
        newRecord.setQuestStatus(QuestStatus.COMPLETED);
        newRecord.setResults("A");
        newRecord.setObjectiveStatus(questRecord.getObjectiveStatus());
        questRepository.save(newRecord);
        questRepository.delete(questRecord);
        record.setSchoolId(schoolId);
        return record;
    }

    private int getAbilityLevelUpperLimit(Player player) {
        return player.getPlayerLevel();
    }

    @Override
    public ParameterSpace createParameterSpace(long accountId) {
        return schoolRepository.findById(accountId)
                .map(it -> it.createParameterSpace(resourceContext))
                .orElse(ParameterSpace.EMPTY);
    }

}
