/*
 * Created 2018-9-26 16:46:28
 */
package cn.com.yting.kxy.web.party;

import java.util.List;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import cn.com.yting.kxy.web.message.WebsocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/party")
public class PartyController implements ModuleApiProvider {

    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private SupportRelationRepository supportRelationRepository;
    @Autowired
    private SupportLogRepository supportLogRepository;

    @Autowired
    private PartyService partyService;
    @Autowired
    private WebsocketMessageService websocketMessageService;

    @Autowired
    private TimeProvider timeProvider;

    @RequestMapping("/view/myself")
    public PartyComplex viewMyself(@AuthenticationPrincipal Account account) {
        List<SupportRelation> supportRelations = supportRelationRepository.findByInviterAccountId(account.getId()).stream()
                .filter(it -> !it.isReleased())
                .collect(Collectors.toList());
        int supporterForOthersCount = supportRelationRepository.countBySupporter(account.getId());
        return new PartyComplex(partyRepository.findOrCreateById(account.getId()), supportRelations, supporterForOthersCount);
    }

    @PostMapping("/requestCandidates")
    public PartyRecord requestCandidates(
            @AuthenticationPrincipal Account account,
            @RequestParam("highLevel") boolean highLevel
    ) {
        return partyService.requestCandidates(account.getId(), highLevel);
    }

    @PostMapping("/requestCandidatesInFriends")
    public PartyRecord requestCandidatesInFriends(
            @AuthenticationPrincipal Account account,
            @RequestParam("highLevel") boolean highLevel
    ) {
        return partyService.requestCandidatesInFriends(account.getId(), highLevel);
    }

    @PostMapping("/invite")
    public SupportRelation invite(
            @AuthenticationPrincipal Account account,
            @RequestParam("targetAccountId") long targetAccountId
    ) {
        return partyService.invite(account.getId(), targetAccountId);
    }

    @PostMapping("/release")
    public WebMessageWrapper release(
            @AuthenticationPrincipal Account account,
            @RequestParam("targetAccountId") long targetAccountId
    ) {
        partyService.release(account.getId(), targetAccountId);
        return WebMessageWrapper.ok();
    }

    @PostMapping("/resolveSupportReward")
    public PartyRecord resolveSupportReward(@AuthenticationPrincipal Account account) {
        return partyService.resolveSupportReward(account.getId());
    }

    @PostMapping("/obtainSupportReward")
    public PartyRecord obtainSupportReward(@AuthenticationPrincipal Account account) {
        return partyService.obtainSupportReward(account.getId());
    }

    @RequestMapping("/latestSupportLogs")
    public List<SupportLog> latestSupportLogs(@AuthenticationPrincipal Account account) {
        return supportLogRepository.findBySupportAccountIdOrdered(account.getId(), PageRequest.of(0, 20));
    }

    @RequestMapping("/todaySupportReward")
    public long todaySupportReward(@AuthenticationPrincipal Account account) {
        long todayFeeSum = supportLogRepository.getFeeSumOfDate(account.getId(), timeProvider.today());
        long reward = (long) (todayFeeSum * PartyConstants.REWARD_RATE);
        reward = Math.min(reward, PartyConstants.DAILY_REWARD_GOLD_MAX);
        return reward;
    }

    @TransactionalEventListener
    public void onSupportExpired(SupportExpiredEvent event) {
        event.getExpiredSupportRelations().stream()
                .filter(it -> !it.isReleased())
                .forEach(it -> {
                    websocketMessageService.sendToUser(it.getInviterAccountId(), "/party/supportExpired", it);
                });
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
                .name("party")
                .baseUri("/party")
                //
                .webInterface()
                .name("viewMyself")
                .uri("/view/myself")
                .description("获得自己的助战队伍信息")
                .response(PartyComplex.class, "助战队伍复合信息")
                .and()
                //
                .webInterface()
                .uri("/requestCandidates")
                .post()
                .description("请求一批助战备选")
                .requestParameter("boolean", "highLevel", "是否请求高战力备选列表")
                .response(PartyRecord.class, "助战队伍信息")
                .and()
                //
                .webInterface()
                .uri("/requestCandidatesInFriends")
                .post()
                .description("从好友中请求一批助战备选")
                .requestParameter("boolean", "highLevel", "是否请求高战力备选列表")
                .response(PartyRecord.class, "助战队伍信息")
                .and()
                //
                .webInterface()
                .uri("/invite")
                .post()
                .description("邀请一名玩家助战")
                .requestParameter("integer", "targetAccountId", "要邀请的目标的账号id")
                .response(SupportRelation.class, "邀请成功的助战关系")
                .and()
                //
                .webInterface()
                .uri("/release")
                .post()
                .description("解除一名玩家助战")
                .requestParameter("integer", "targetAccountId", "要解除的目标的账号id")
                .and()
                //
                .webInterface()
                .uri("/resolveSupportReward")
                .post()
                .description("结算前一天的助战奖励")
                .response(PartyRecord.class, "助战队伍信息")
                .and()
                //
                .webInterface()
                .uri("/obtainSupportReward")
                .post()
                .description("领取当前可领取的助战奖励")
                .response(PartyRecord.class, "助战队伍信息")
                .and()
                //
                .webInterface()
                .uri("/latestSupportLogs")
                .description("获得最近为他人助战的记录")
                .responseArray(SupportLog.class, "助战记录")
                .and()
                //
                .webInterface()
                .uri("/todaySupportReward")
                .description("获得今天的助战奖励")
                .response("number", "助战奖励值")
                .and()
                //
                //
                //
                .webNotification()
                .queue("/party/supportExpired")
                .description("助战队友离队的通知")
                .messageType(SupportRelation.class);
    }
}
