/*
 * Created 2018-8-1 18:23:25
 */
package cn.com.yting.kxy.web.quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resetting.ResetType;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.account.AccountCreatedEvent;
import cn.com.yting.kxy.web.award.AwardService;
import cn.com.yting.kxy.web.battle.BattleRepository;
import cn.com.yting.kxy.web.battle.BattleSession;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyRecord;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.quest.model.AwardConfig;
import cn.com.yting.kxy.web.quest.model.QuestStatus;
import cn.com.yting.kxy.web.quest.model.StatusConfig;
import cn.com.yting.kxy.web.quest.model.objective.BattleObjective;
import cn.com.yting.kxy.web.quest.model.objective.CurrencyObjective;
import cn.com.yting.kxy.web.quest.model.objective.LevelObjective;
import cn.com.yting.kxy.web.quest.model.objective.NullObjective;
import cn.com.yting.kxy.web.quest.model.objective.Objective;
import cn.com.yting.kxy.web.quest.resource.RandQuestBehaAndCondCollections;
import cn.com.yting.kxy.web.quest.resource.RandQuestBehaAndConds;
import cn.com.yting.kxy.web.quest.resource.SequenceQuest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class QuestService implements ResetTask {

    private static final Logger LOG = LoggerFactory.getLogger(QuestService.class);

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private QuestRepository questRepository;
    @Autowired
    private BattleRepository battleRepository;

    @Autowired
    private AwardService awardService;
    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final ScriptEngineFactory scriptEngineFactory;

    public QuestService() {
        ScriptEngineManager manager = new ScriptEngineManager();
        scriptEngineFactory = manager.getEngineByName("javascript").getFactory();
    }

    public QuestRecord startQuest(long accountId, long questId) {
        if (questId >= 730111 && questId <= 730150) {
            throw new IllegalArgumentException();
        }
        return tryStartQuest(accountId, questId, true);
    }

    public QuestRecord startFxjlQuest(long accountId, long questId) {
        return tryStartQuest(accountId, questId, true);
    }

    public QuestRecord achieveObjective(long accountId, long questId, int objectiveIndex, String... args) {
        QuestRecord record = questRepository.findByIdForWrite(accountId, questId)
                .orElseThrow(() -> KxyWebException.notFound("任务不存在"));
        if (!record.getQuestStatus().equals(QuestStatus.IN_PROGRESS)) {
            throw QuestException.illegalStatusForAchieveObjective();
        }
        if (record.isObjectiveCompleted(objectiveIndex)) {
            throw QuestException.objectiveAlreadyDone();
        }

        SequenceQuest definition = SequenceQuest.getFrom(resourceContext, questId);
        List<Objective> objectives = getActualObjectives(definition, record);
        Objective objective = objectives.get(objectiveIndex);
        if (objective instanceof NullObjective) {
            NullObjective nullObjective = (NullObjective) objective;
            processResult(accountId, record, definition, nullObjective.getResult());
            record.setObjectiveCompleted(objectiveIndex);
        } else if (objective instanceof LevelObjective) {
            LevelObjective levelObjective = (LevelObjective) objective;
            if (getPlayerLevel(accountId) < levelObjective.getRequiredLevel()) {
                throw QuestException.objectiveNotReadyToDone();
            }
            processResult(accountId, record, definition, levelObjective.getResult());
        } else if (objective instanceof BattleObjective) {
            BattleObjective battleObjective = (BattleObjective) objective;
            long battleSessionId;
            try {
                battleSessionId = Long.parseLong(args[0]);
            } catch (NumberFormatException ex) {
                LOG.info("尝试完成战斗任务时参数错误，accountId={}", accountId);
                throw KxyWebException.unknown("参数错误");
            }
            BattleSession battleSession = battleRepository.findById(battleSessionId)
                    .orElseThrow(() -> KxyWebException.unknown("战斗会话不存在"));
            if (battleObjective.getBattleDescriptorId() != battleSession.getBattleDescriptorId()) {
                throw KxyWebException.unknown("战斗配置id不正确");
            }
            BattleDirector bd = battleSession.getBattleDirector();
            String result;
            if (bd.getBattleResult().getStatistics().getWinStance() == Unit.Stance.STANCE_RED) {
                result = battleObjective.getWonResult();
            } else {
                result = battleObjective.getLostResult();
            }
            if (result != null) {
                processResult(accountId, record, definition, result);
            }
        } else if (objective instanceof CurrencyObjective) {
            CurrencyObjective currencyObjective = (CurrencyObjective) objective;
            CurrencyRecord currencyRecord = currencyService.findOrCreateRecord(accountId, currencyObjective.getCurrencyId());
            if (currencyRecord.getAmount() < currencyObjective.getCurrencyAmount()) {
                throw QuestException.insufficientCurrency();
            }
            currencyService.decreaseCurrency(accountId, currencyObjective.getCurrencyId(), currencyObjective.getCurrencyAmount(), true, CurrencyConstants.PURPOSE_DECREMENT_提交货币的任务);
            processResult(accountId, record, definition, currencyObjective.getResult());
        }
        return record;
    }

    private void processResult(long accountId, QuestRecord record, SequenceQuest definition, String result) {
        if (record.getResults().contains(result)) {
            return;
        }

        if (record.getQuestId() == 730010 && result.equals("A")) {
            for (long i = 730011; i <= 730110; i++) {
                tryStartQuest(accountId, i, false);
            }
        }

        record.appendResult(result);

        List<Long> awardIds = new ArrayList<>();
        for (AwardConfig awardConfig : definition.getAwardConfigs()) {
            if (!awardConfig.getResults().contains(result)) {
                continue;
            }
            if (awardConfig.getResults().chars().allMatch(c -> record.getResults().contains(String.valueOf((char) c)))) {
                awardIds.add(awardConfig.getAwardId());
            }
        }
        processAward(accountId, awardIds);

        for (Long id : definition.getActivateIds()) {
            tryStartQuest(accountId, id, false);
        }

        for (StatusConfig statusConfig : definition.getStatusConfigs()) {
            if (!statusConfig.getResults().contains(result)) {
                continue;
            }

            if (statusConfig.getResults().chars().allMatch(c -> record.getResults().contains(String.valueOf((char) c)))) {
                QuestRecord changeStatusQuest = questRepository.findByIdForWrite(accountId, statusConfig.getQuestId()).orElse(null);
                if (changeStatusQuest != null) {
                    if (statusConfig.getTransfer().equals(StatusConfig.TRANSFER_COMPLETED)) {
                        complete(changeStatusQuest);
                    } else if (statusConfig.getTransfer().equals(StatusConfig.TRANSFER_RESET)) {
                        reset(changeStatusQuest);
                    }
                }
            }
        }

        questRepository.flush();
    }

    private void processAward(long accountId, List<Long> awardIds) {
        awardIds.forEach(it -> awardService.processAward(accountId, it));
    }

    private void complete(QuestRecord record) {
        record.setQuestStatus(QuestStatus.COMPLETED);

        eventPublisher.publishEvent(new QuestCompletedEvent(this, record));
    }

    private void reset(QuestRecord record) {
        record.setQuestStatus(QuestStatus.NOT_STARTED_YET);
        record.setResults("");

        tryStartQuest(record.getAccountId(), record.getQuestId(), false);
    }

    public QuestRecord tryStartQuest(long accountId, long questId, boolean throwException) {
        QuestRecord record = questRepository.findByIdForWrite(accountId, questId).orElse(null);
        SequenceQuest definition = SequenceQuest.getFrom(resourceContext, questId);
        if (record != null) {
            if (!record.getQuestStatus().equals(QuestStatus.NOT_STARTED_YET)) {
                if (throwException) {
                    throw QuestException.illegalStatusForStart();
                } else {
                    return null;
                }
            }
            if (definition.getMaxPickupCount() != null && definition.getMaxPickupCount() <= record.getStartedCount()) {
                if (throwException) {
                    throw QuestException.reachStartLimit();
                } else {
                    return null;
                }
            }
        }

        if (getPlayerLevel(accountId) < definition.getPickupLvRequire()) {
            if (throwException) {
                throw QuestException.notMeetPrerequirement();
            } else {
                return null;
            }
        }
        if (!definition.getPreQuestResults().isEmpty()) {
            Map<Long, String> resultMap = questRepository.findByAccountId(accountId).stream()
                    .collect(Collectors.toMap(QuestRecord::getQuestId, QuestRecord::getResults));
            ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
            Bindings bindings = scriptEngine.createBindings();
            bindings.put("map", resultMap);
            scriptEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
            try {
                scriptEngine.eval(
                        "function r(id, result) {"
                        + "var a = map.get(java.lang.Long.valueOf(id));"
                        + "return a ? a.contains(result) : false;"
                        + "}"
                );
                if (!(Boolean) scriptEngine.eval(definition.getPreQuestResults())) {
                    if (throwException) {
                        throw QuestException.notMeetPrerequirement();
                    } else {
                        return null;
                    }
                }
            } catch (ScriptException | ClassCastException ex) {
                throw KxyWebException.unknown("判断前置条件时发生脚本错误", ex);
            }
        }

        record = questRepository.save(createOrResetQuestRecord(record, accountId, questId, definition));
        record.setStartedCount(record.getStartedCount() + 1);

        eventPublisher.publishEvent(new QuestStartedEvent(this, record));

        return record;
    }

    private int getPlayerLevel(long accountId) {
        return playerRepository.findById(accountId).map(Player::getPlayerLevel).orElse(0);
    }

    private QuestRecord createOrResetQuestRecord(QuestRecord record, long accountId, long questId, SequenceQuest definition) {
        if (record == null) {
            record = new QuestRecord();
            record.setAccountId(accountId);
            record.setQuestId(questId);
        }
        record.setQuestStatus(QuestStatus.IN_PROGRESS);
        record.setResults("");

        List<Objective> objectives;
        if (definition.isFixedObjective()) {
            objectives = definition.getFixedObjectives();
        } else {
            RandQuestBehaAndConds randomBac = RandQuestBehaAndCondCollections.getFrom(resourceContext, definition.getRandBehaAndCond()).getSelector().getSingle();
            record.setRandomBacId(randomBac.getId());
            objectives = randomBac.getFixedObjectives();
        }
        int size = objectives.size();
        char[] chars = new char[size];
        Arrays.fill(chars, 'F');
        record.setObjectiveStatus(new String(chars));

        return record;
    }

    private List<Objective> getActualObjectives(SequenceQuest definition, QuestRecord record) {
        if (definition.isFixedObjective()) {
            return definition.getFixedObjectives();
        } else {
            Long randomBacId = record.getRandomBacId();
            return RandQuestBehaAndConds.getFrom(resourceContext, randomBacId).getFixedObjectives();
        }
    }

    @EventListener
    public void onAccountCreated(AccountCreatedEvent event) {
        startQuest(event.getAccount().getId(), QuestConstants.ID_ROOT_QUEST);
    }

    @Override
    public void anyReset(ResetType resetType) {
        resetType.filterStream(resourceContext.getLoader(SequenceQuest.class).getAll().values())
                .forEach(definition -> {
                    int count = questRepository.resetStartedCountByQuestId(definition.getId());
                    LOG.info("对任务 {} 的领取次数进行重置，重置了 {} 条记录", definition.getId(), count);
                });
    }
}
