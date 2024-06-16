/*
 * Created 2019-1-7 17:03:54
 */
package cn.com.yting.kxy.web.perk;

import java.util.Random;
import java.util.stream.IntStream;

import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.player.ParameterSpaceProvider;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.school.SchoolRecord;
import cn.com.yting.kxy.web.school.SchoolRepository;
import cn.com.yting.kxy.web.perk.resource.TalentTrainModel;
import cn.com.yting.kxy.web.perk.resource.TalentTrainModelLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class PerkService implements ParameterSpaceProvider {

    @Autowired
    private PerkRingRepository perkRingRepository;
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ResourceContext resourceContext;

    public PerkRing createPerkRing(long accountId) {
        if (perkRingRepository.existsById(accountId)) {
            throw new PerkException(PerkException.EC_天赋记录已存在, "天赋记录已存在");
        }
        Player player = playerRepository.findById(accountId).get();
        if (player.getPlayerLevel() < 60) {
            throw KxyWebException.unknown("等级不足");
        }

        PerkRing perkRing = new PerkRing();
        perkRing.setAccountId(accountId);
        perkRing.setProgress(0);
        for (int i = 0; i < 9; i++) {
            perkRing.setPerkSelection(i, PerkSelection.NONE);
        }

        return perkRingRepository.save(perkRing);
    }

    public PerkRing makeProgress(long accountId, long amountToConsume) {
        PerkRing perkRing = perkRingRepository.findById(accountId).orElseThrow(() -> KxyWebException.notFound("天赋记录不存在"));
        final int cap = 980550;
        if (perkRing.getProgress() >= cap) {
            throw new PerkException(PerkException.EC_天赋培养已达上限, "天赋培养已达上限");
        }
        final long lowestConsumption = 1;
        if (amountToConsume < lowestConsumption) {
            throw new PerkException(PerkException.EC_消耗量太低, "消耗量太低");
        }

        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_九灵仙丹, amountToConsume);
        Random random = RandomProvider.getRandom();
        int progress = perkRing.getProgress();
        final int a = 1;
        progress += IntStream.generate(() -> (random.nextInt(5) + 1) * a)
            .limit(amountToConsume)
            .sum();
        if (progress > cap) {
            progress = cap;
        }
        perkRing.setProgress(progress);

        return perkRing;
    }

    public PerkRing makePerkSelection(long accountId, int index, PerkSelection selection) {
        PerkRing perkRing = perkRingRepository.findById(accountId).orElseThrow(() -> KxyWebException.notFound("天赋记录不存在"));
        TalentTrainModel model = resourceContext.getByLoaderType(TalentTrainModelLoader.class).findModel(perkRing.getProgress());
        if (model.getPositionStar(index) < 10) {
            throw new PerkException(PerkException.EC_位置未达到等级要求, "位置未达到等级要求");
        }
        if (!perkRing.getPerkSelection(index).equals(PerkSelection.NONE)) {
            throw new PerkException(PerkException.EC_位置已激活, "位置已激活");
        }

        perkRing.setPerkSelection(index, selection);

        return perkRing;
    }

    public PerkRing switchPerkSelection(long accountId, int index, PerkSelection selection) {
        PerkRing perkRing = perkRingRepository.findById(accountId).orElseThrow(() -> KxyWebException.notFound("天赋记录不存在"));
        TalentTrainModel model = resourceContext.getByLoaderType(TalentTrainModelLoader.class).findModel(perkRing.getProgress());
        if (model.getPositionStar(index) < 10) {
            throw new PerkException(PerkException.EC_位置未达到等级要求, "位置未达到等级要求");
        }

        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, 5000);
        perkRing.setPerkSelection(index, selection);

        return perkRing;
    }

    public PerkRingDetail perkRingToDetail(PerkRing perkRing) {
        SchoolRecord schoolRecord = schoolRepository.findById(perkRing.getAccountId()).get();
        return PerkRingDetail.fromPerkRing(perkRing, schoolRecord.getSchoolId(), resourceContext);
    }

    @Override
    public ParameterSpace createParameterSpace(long accountId) {
        PerkRing perkRing = perkRingRepository.findById(accountId).orElse(null);
        SchoolRecord schoolRecord = schoolRepository.findById(accountId).orElse(null);
        if (perkRing != null && schoolRecord != null) {
            return PerkRingDetail.fromPerkRing(perkRing, schoolRecord.getSchoolId(), resourceContext).toParameterSpace();
        } else {
            return ParameterSpace.EMPTY;
        }
    }
}
