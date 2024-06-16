/*
 * Created 2018-10-16 15:59:48
 */
package cn.com.yting.kxy.web.activity;

import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resetting.ResetType;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.activity.resource.ActivityOtherInfo;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.game.antique.AntiqueBuyEvent;
import cn.com.yting.kxy.web.game.baccarat.BaccaratLotteryEvent;
import cn.com.yting.kxy.web.game.brawl.BrawlEndEvent;
import cn.com.yting.kxy.web.game.fishing.FishingFinishEvent;
import cn.com.yting.kxy.web.game.goldTower.GoldTowerChallengeSuccessEvent;
import cn.com.yting.kxy.web.game.kuaibidazhuanpan.KbdzpMadeTurnEvent;
import cn.com.yting.kxy.web.game.mineExploration.MineExplorationStartEvent;
import cn.com.yting.kxy.web.game.minearena.MineArenaChallengeStartEvent;
import cn.com.yting.kxy.web.game.minearena.MineArenaRewardObtainEvent;
import cn.com.yting.kxy.web.game.mingjiandahui.MjdhService;
import cn.com.yting.kxy.web.game.zaixianjiangli.ZxjlService;
import cn.com.yting.kxy.web.game.yuanxiaojiayao.YxjyService;
import cn.com.yting.kxy.web.game.fuxingjianglin.FxjlService;
import cn.com.yting.kxy.web.game.mingjiandahui.MjdhBattleEndEvent;
import cn.com.yting.kxy.web.game.slots.SlotsTakePrizeEvent;
import cn.com.yting.kxy.web.game.treasure.TreasureObtainedEvent;
import cn.com.yting.kxy.web.game.yibenwanli.YibenwanliBuyEvent;
import cn.com.yting.kxy.web.quest.QuestCompletedEvent;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class ActivityService implements ResetTask {

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ActivityPlayerRepository activityPlayerRepository;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    @Lazy
    private MjdhService mjdhService;
    @Autowired
    @Lazy
    private ZxjlService zxjlService;
    @Autowired
    @Lazy
    private YxjyService yxjyService;
    @Autowired
    @Lazy
    private FxjlService fxjlService;

    @Autowired
    private ResourceContext resourceContext;

    @EventListener
    public void onQuestCompleted(QuestCompletedEvent event) {
        long accountId = event.getQuestRecord().getAccountId();
        long questId = event.getQuestRecord().getQuestId();
        if (questId >= 720000 && questId <= 720026) {
            makeProgress(accountId, 157001, 20);
        }
    }

    @EventListener
    public void onGoldTowerChallengeSuccess(GoldTowerChallengeSuccessEvent event) {
        long accountId = event.getGoldTowerChallengeEntity().getAccountId();
        makeProgress(accountId, 157002, 20, event.getSuccessCount());
    }

    @EventListener
    public void onYibenwanliBuy(YibenwanliBuyEvent event) {
        long accountId = event.getYibenwanliRecord().getAccountId();
//        makeProgress(accountId, 157003, 5);
    }

    @EventListener
    public void onKbdzpMadeTurn(KbdzpMadeTurnEvent event) {
        long accountId = event.getKbdzpRecord().getAccountId();
        makeProgress(accountId, 157004, 10);
    }

    @EventListener
    public void onMineArenaChallengeStart(MineArenaChallengeStartEvent event) {
        long accountId = event.getMineArenaRecord().getAccountId();
        makeProgress(accountId, 157006, 5);
    }

    @EventListener
    public void onMineArenaRewardObtained(MineArenaRewardObtainEvent event) {
        long accountId = event.getAccountId();
        makeProgress(accountId, 157006, 5, 5);
    }

    @EventListener
    public void onTreasureObtained(TreasureObtainedEvent event) {
        long accountId = event.getAccountId();
        makeProgress(accountId, 157007, 1);
    }

    @EventListener
    public void onAntiqueBuy(AntiqueBuyEvent event) {
        long accountId = event.getAntiqueRecord().getAccountId();
//        makeProgress(accountId, 158001, 3);
    }

    @EventListener
    public void onBrawlEnd(BrawlEndEvent event) {
        long accountId = event.getBrawlRecord().getAccountId();
        makeProgress(accountId, 157008, 1);
    }

    @EventListener
    public void onFishingFinish(FishingFinishEvent event) {
        long accountId = event.getFishingRecord().getAccountId();
        makeProgress(accountId, 157009, 10);
    }

    @EventListener
    public void onSlotsTakePrize(SlotsTakePrizeEvent event) {
        long accountId = event.getSlotsRecord().getAccountId();
//        makeProgress(accountId, 158002, 3);
    }

    @EventListener
    public void onMjdhBattleEnd(MjdhBattleEndEvent event) {
        event.getAccountIds().forEach(accountId -> makeProgress(accountId, 158003, 10));
    }

    @EventListener
    public void onMineExplorationStart(MineExplorationStartEvent event) {
        long accountId = event.getRecord().getAccountId();
//        makeProgress(accountId, 158004, 1);
    }

    @EventListener
    public void onBaccaratLottery(BaccaratLotteryEvent event) {
        long accountId = event.getBaccaratBet().getAccountId();
//        makeProgress(accountId, 158006, (int) KuaibiUnits.toKuaibi(event.getBaccaratBet().getBetsSum()));
    }

    public void makeProgress(long accountId, long activityId, int goal) {
        makeProgress(accountId, activityId, goal, 1);
    }

    public void makeProgress(long accountId, long activityId, int goal, int makeCount) {
        ActivityRecord activityRecord = activityRepository.findOrCreateById(accountId, activityId);
        for (int i = 0; i < makeCount; i++) {
            activityRecord.increaseProgress();
        }
        if (!activityRecord.isCompleted() && activityRecord.getProgress() >= goal) {
            activityRecord.setCompleted(true);
            ActivityOtherInfo activityOtherInfo = resourceContext.getLoader(ActivityOtherInfo.class).get(activityId);
            giveActivePoints(accountId, activityOtherInfo.getLivenessAward());
        }
    }

    public List<Long> getOpeningActivityIds() {
        List<Long> openingActivityIds = new ArrayList<>();
        // 添加欢乐大转盘和邀请分享的id
        openingActivityIds.add(157004L);
        openingActivityIds.add(157005L);
        openingActivityIds.add(158003L);
        //
//        if (mjdhService.isAvailable()) {
//            openingActivityIds.add(158003L);
//        }
        if (zxjlService.isAvailable()) {
            openingActivityIds.add(158006L);
        }
        if (yxjyService.isAvailable()) {
            openingActivityIds.add(158005L);
        }
        if (fxjlService.isAvailable()) {
            openingActivityIds.add(158007L);
        }
        return openingActivityIds;
    }

    private void giveActivePoints(long accountId, long points) {
        ActivityPlayerRecord playerRecord = activityPlayerRepository.findOrCreateById(accountId);
        long beforePoints = playerRecord.getIncomingActivePoints();
        playerRecord.increaseIncomingActivePoints(points);
        if (beforePoints <= ActivityConstants.MAX_ACTIVE_POINTS_EARNING_PER_DAY) {
            long assumedAfterPoints = Math.min(playerRecord.getIncomingActivePoints(), ActivityConstants.MAX_ACTIVE_POINTS_EARNING_PER_DAY);
            currencyService.increaseCurrency(accountId, CurrencyConstants.ID_活跃点, assumedAfterPoints - beforePoints);
        }
    }

    @Override
    public void anyReset(ResetType resetType) {
        resetType.filterStream(resourceContext.getLoader(ActivityOtherInfo.class).getAll().values())
                .forEach(it -> activityRepository.resetProgressByActivityId(it.getId()));
    }

    @Override
    public void dailyReset() {
        activityPlayerRepository.resetPoints();
    }

}
