/*
 * Created 2018-7-10 16:03:03
 */
package cn.com.yting.kxy.web.invitation;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.controller.ControllerUtils;
import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/invitation")
public class InvitationController implements ModuleApiProvider {

    @Autowired
    private InviterRepository inviterRepository;
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private InvitationRewardLogRepository invitationRewardLogRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private InvitationService invitationService;
    @Autowired
    private CompositeInvitationService compositeInvitationService;

    @Autowired
    private TimeProvider timeProvider;

    @RequestMapping("/view/myself")
    public InvitationInfo viewMyself(@AuthenticationPrincipal Account account) {
        InviterRecord record = inviterRepository.findById(account.getId())
            .orElseThrow(ControllerUtils::notFoundException);
        int count = invitationRepository.countByInviterIdAndInviterDepth(account.getId(), 1);
        return new InvitationInfo(record, count);
    }

    @PostMapping("/create")
    public InviterRecord create(
        @AuthenticationPrincipal Account account,
        @RequestParam(name = "invitationCode", required = false) String invitationCode
    ) {
        return invitationService.createInviterRecord(account.getId(), invitationCode);
    }

    @RequestMapping("/verify")
    public WebMessageWrapper verifyInvitationCode(@RequestParam(name = "invitationCode") String invitationCode) {
        invitationService.verifyInvitationCodeAndGetInviterRecord(invitationCode);
        return WebMessageWrapper.ok();
    }

    @PostMapping("/resolveInvitationReward")
    public InviterRecord resolveInvitationReward(@AuthenticationPrincipal Account account) {
        return compositeInvitationService.resolveInvitationReward(account.getId());
    }

    @PostMapping("/obtainInvitationReward")
    public InviterRecord obtainInvitationReward(@AuthenticationPrincipal Account account) {
        return compositeInvitationService.obtainInvitationReward(account.getId());
    }

    @RequestMapping("/todayInvitationRewardLogs")
    public List<InvitationRewardLog> todayInvitationRewardLogs(@AuthenticationPrincipal Account account) {
        return invitationRewardLogRepository.findInDate(account.getId(), timeProvider.today());
    }

    @RequestMapping("/groupedKbdzpReward")
    public GroupedInvitationReward groupedTodayKbdzpReward(@AuthenticationPrincipal Account account) {
        return queryGroupedInvitationRward(account.getId(), InvitationRewardLog::getKbdzpEnergyReward);
    }

    @RequestMapping("/groupedKuaibiRewardLogs")
    public GroupedInvitationReward groupedTodayKuaibiReward(@AuthenticationPrincipal Account account) {
        return queryGroupedInvitationRward(account.getId(), InvitationRewardLog::getKuaibiReward);
    }

    @PostMapping("/extendInvitationLimit")
    public InviterRecord extendInvitationLimit(@AuthenticationPrincipal Account account) {
        return compositeInvitationService.extendInvitationLimit(account.getId());
    }

    private GroupedInvitationReward queryGroupedInvitationRward(long accountId, Function<InvitationRewardLog, Integer> rewardValueMapper) {
        List<InvitationRewardLog> logs = invitationRewardLogRepository.findInDate(accountId, timeProvider.today());
        List<GroupedInvitationReward> rewards = invitationRepository.findDirectInvitationByInviterId(accountId).stream()
            .map(record -> {
                long directInviteeId = record.getAccountId();
                int directInviteeReward = logs.stream()
                    .filter(r -> r.getInviteeId() == directInviteeId)
                    .findAny()
                    .map(rewardValueMapper)
                    .orElse(0);
                List<GroupedInvitationReward> secondLevelInviteeRewards = invitationRepository.findDirectInvitationByInviterId(directInviteeId).stream()
                    .map(InvitationRecord::getAccountId)
                    .map(id -> logs.stream()
                        .filter(r -> r.getInviteeId() == id)
                        .findAny().orElse(null)
                    )
                    .filter(Objects::nonNull)
                    .map(r -> new GroupedInvitationReward(
                        r.getInviteeId(),
                        playerRepository.findById(r.getInviteeId()).map(Player::getPlayerName).orElse(null),
                        rewardValueMapper.apply(r),
                        null,
                        rewardValueMapper.apply(r)
                    ))
                    .collect(Collectors.toList());
                return new GroupedInvitationReward(
                    directInviteeId,
                    playerRepository.findById(directInviteeId).map(Player::getPlayerName).orElse(null),
                    directInviteeReward,
                    secondLevelInviteeRewards,
                    secondLevelInviteeRewards.stream()
                        .mapToInt(GroupedInvitationReward::getSum)
                        .sum() + directInviteeReward
                );
            })
            .collect(Collectors.toList());
        return new GroupedInvitationReward(
            0,
            null,
            0,
            rewards,
            rewards.stream()
                .mapToInt(GroupedInvitationReward::getSum)
                .sum()
        );
    }

    @Value
    @WebMessageType
    public static class GroupedInvitationReward {

        private long accountId;
        private String playerName;
        private int value;
        private List<GroupedInvitationReward> children;
        private int sum;
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .baseUri("/invitation")
            //
            .webInterface()
            .name("viewMyself")
            .uri("/view/myself")
            .description("获取自己的邀请者信息")
            .response(InvitationInfo.class, "自己的邀请者信息")
            .expectableError(KxyWebException.EC_NOT_FOUND, "自己的邀请者信息不存在")
            .and()
            //
            .webInterface()
            .uri("/create")
            .post()
            .description("创建自己的邀请者信息（保留作测试用）")
            .requestParameter("string", "invitationCode", "上游邀请者的邀请码（可选）")
            .response(InviterRecord.class, "成功创建的自己的邀请者信息")
            .expectableError(InvitationException.EC_INVITER_RECORD_EXISTED, "邀请者记录已存在")
            .expectableError(InvitationException.EC_INVITATION_CODE_NOT_VALID, "邀请码无效")
            .expectableError(InvitationException.EC_INVIER_REACH_LIMIT, "邀请者已达到邀请数量限制")
            .and()
            //
            .webInterface()
            .uri("/verify")
            .description("校验邀请码是否可用")
            .requestParameter("string", "invitationCode", "邀请码")
            .expectableError(InvitationException.EC_INVITATION_CODE_NOT_VALID, "邀请码无效")
            .expectableError(InvitationException.EC_INVIER_REACH_LIMIT, "邀请者已达到邀请数量限制")
            .and()
            //
            .webInterface()
            .uri("/resolveInvitationReward")
            .post()
            .description("结算邀请奖励")
            .response(InviterRecord.class, "自己的邀请者信息")
            .expectableError(InvitationException.EC_INVITER_RECORD_NOT_EXISTED, "自己的邀请者信息不存在")
            .and()
            //
            .webInterface()
            .uri("/obtainInvitationReward")
            .post()
            .description("领取邀请奖励")
            .response(InviterRecord.class, "自己的邀请者信息")
            .expectableError(InvitationException.EC_ALREADY_DELIVERED, "当天已经领取过了")
            .expectableError(InvitationException.EC_INVITER_RECORD_NOT_EXISTED, "自己的邀请者信息不存在")
            .and()
            //
            .webInterface()
            .uri("/todayInvitationRewardLogs")
            .description("获得今天的邀请回报记录集（今天结算的是昨天的奖励）")
            .responseArray(InvitationRewardLog.class, "回报记录的集合")
            .and()
            //
            .webInterface()
            .uri("/groupedKbdzpReward")
            .description("获得今天的块币大转盘能量邀请回报分组记录")
            .response(GroupedInvitationReward.class, "回报分组记录")
            .and()
            //
            .webInterface()
            .uri("/groupedKuaibiRewardLogs")
            .description("获得今天的块币邀请回报分组记录")
            .response(GroupedInvitationReward.class, "回报分组记录")
            .and()
            //
            .webInterface()
            .uri("/extendInvitationLimit")
            .post()
            .description("扩展一次邀请上限")
            .response(InviterRecord.class, "邀请者记录")
            .expectableError(InvitationException.EC_INVITATION_LIMIT_REACH_MAX, "邀请上限已达最大")
            .and();
    }

}
