/*
 * Created 2018-10-19 10:48:25
 */
package cn.com.yting.kxy.web.game.minearena;

import java.util.List;

import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.controller.ControllerUtils;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/arena")
public class MineArenaController implements ModuleApiProvider {

    @Autowired
    private MineArenaRepository mineArenaRepository;
    @Autowired
    private PitRepository pitRepository;
    @Autowired
    private PitPositionChangeLogRepository pitPositionChangeLogRepository;
    @Autowired
    private MineArenaRewardObtainLogRepository mineArenaRewardObtainLogRepository;
    @Autowired
    private MineArenaChallengeLogRepository mineArenaChallengeLogRepository;

    @Autowired
    private MineArenaService mineArenaService;

    @RequestMapping("/view/myself")
    public MineArenaComplex viewMyself(@AuthenticationPrincipal Account account) {
        MineArenaRecord arenaRecord = mineArenaRepository.findById(account.getId()).orElseThrow(ControllerUtils::notFoundException);
        Pit pit = pitRepository.findByAccountId(account.getId()).orElseThrow(ControllerUtils::notFoundException);
        return new MineArenaComplex(arenaRecord, mineArenaService.wrapPitWithDetail(pit));
    }

    @RequestMapping("/viewRanking")
    public List<PitDetail> viewRanking(Pageable pageable) {
        if (pageable.getPageSize() > 100) {
            throw KxyWebException.unknown("分页过大");
        }
        return mineArenaService.wrapPitsWithDetail(pitRepository.findOrderByPosition(pageable));
    }

    @PostMapping("/createRecord")
    public MineArenaComplex createRecord(@AuthenticationPrincipal Account account) {
        return mineArenaService.createRecord(account.getId());
    }

    @RequestMapping("/randomCandidates")
    public List<PitDetail> randomCandidates(@AuthenticationPrincipal Account account) {
        return mineArenaService.findRandomValidChallengingPits(account.getId());
    }

    @RequestMapping("/currentReward")
    public List<CurrencyStack> currentReward(@AuthenticationPrincipal Account account) {
        return mineArenaService.getTodayRewardUntilNow(account.getId());
    }

    @PostMapping("/startChallenge")
    public StartChallengeResult startChallenge(
        @AuthenticationPrincipal Account account,
        @RequestParam("targetPosition") long targetPosition,
        @RequestParam("kuaibiToUse") long kuaibiToUse
    ) {
        return mineArenaService.startChallenge(account.getId(), targetPosition, kuaibiToUse);
    }

    @PostMapping("/resolveReward")
    public MineArenaRecord  resolveReward(@AuthenticationPrincipal Account account) {
        return mineArenaService.resolveReward(account.getId());
    }

    @PostMapping("/obtainReward")
    public MineArenaRecord obtainReward(@AuthenticationPrincipal Account account) {
        return mineArenaService.obtainReward(account.getId());
    }

    @RequestMapping("/logs")
    public MineArenaLogComplex logs(@AuthenticationPrincipal Account account) {
        List<PitPositionChangeLog> pitPositionChangeLogs = pitPositionChangeLogRepository.findByAccountIdOrderByEventTimeDesc(account.getId(), PageRequest.of(0, 20));
        List<MineArenaRewardObtainLog> mineArenaRewardObtainLogs = mineArenaRewardObtainLogRepository.findByAccountIdOrderByEventTimeDesc(account.getId(), PageRequest.of(0, 20));
        List<MineArenaChallengeLog> mineArenaChallengeLogs = mineArenaChallengeLogRepository.findByAccountIdOrderByEventTimeDesc(account.getId(), PageRequest.of(0, 20));
        return new MineArenaLogComplex(pitPositionChangeLogs, mineArenaRewardObtainLogs, mineArenaChallengeLogs);
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .name("竞技场")
            .baseUri("/arena")
            //
            .webInterface()
            .name("viewMyself")
            .uri("/view/myself")
            .description("查询自己的竞技场记录")
            .response(MineArenaComplex.class, "竞技场复合记录")
            .and()
            //
            .webInterface()
            .uri("/viewRanking")
            .description("查询竞技场排名记录")
            .requestPagenationParameters()
            .responseArray(PitDetail.class, "竞技场的排名记录")
            .and()
            //
            .webInterface()
            .uri("/randomCandidates")
            .description("获取一些备选挑战位置")
            .responseArray(PitDetail.class, "竞技场的排名记录")
            .and()
            //
            .webInterface()
            .uri("/createRecord")
            .post()
            .description("创建竞技场记录")
            .response(MineArenaComplex.class, "竞技场记录")
            .and()
            //
            .webInterface()
            .uri("/currentReward")
            .description("获取今天到目前为止的奖励")
            .responseArray(CurrencyStack.class, "表示奖励的货币堆")
            .and()
            //
            .webInterface()
            .uri("/startChallenge")
            .post()
            .description("开始挑战")
            .requestParameter("number", "targetPosition", "要挑战的位置")
            .requestParameter("number", "kuaibiToUse", "要使用的块币")
            .response(StartChallengeResult.class, "开始挑战的结果")
            .and()
            //
            .webInterface()
            .uri("/resolveReward")
            .post()
            .description("结算前一天的奖励")
            .response(MineArenaRecord.class, "竞技场记录")
            .and()
            //
            .webInterface()
            .uri("/obtainReward")
            .post()
            .description("领取前一天的奖励")
            .response(MineArenaRecord.class, "竞技场记录")
            .and()
            //
            .webInterface()
            .uri("/logs")
            .description("获取最近关于自己的事件的日志")
            .response(MineArenaLogComplex.class, "日志复合记录")
            .and()
            ;
    }
}
