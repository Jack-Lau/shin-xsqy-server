/*
 * Created 2018-7-16 16:07:04
 */
package cn.com.yting.kxy.web.invitation;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.util.TimeUtils;
import cn.com.yting.kxy.web.currency.CurrencyChangeLogRepository;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.game.kuaibidazhuanpan.KbdzpConstants;
import cn.com.yting.kxy.web.game.kuaibidazhuanpan.KbdzpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class CompositeInvitationService {

    @Autowired
    private InviterRepository inviterRepository;
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private InvitationRewardLogRepository invitationRewardLogRepository;
    @Autowired
    private CurrencyChangeLogRepository currencyChangeLogRepository;

    @Autowired
    private KbdzpService kbdzpService;
    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private TimeProvider timeProvider;

    /**
     * 结算邀请关系的奖励，即被邀请者的消费对邀请者的回扣。 只有上次结算日期在当前日期的一天以前才处理
     *
     * @param accountId
     * @return
     */
    public InviterRecord resolveInvitationReward(long accountId) {
        InviterRecord record = inviterRepository.findById(accountId).orElseThrow(InvitationException::inviterRecordNotExisted);
        OffsetDateTime lastResolveTime = TimeUtils.toOffsetTime(record.getLastRewardResolveTime());
        OffsetDateTime currentTime = timeProvider.currentOffsetDateTime();
        if (!lastResolveTime.toLocalDate().isBefore(currentTime.toLocalDate())) {
            return record;
        }

        LocalDate yesterday = currentTime.toLocalDate().minusDays(1);
        AtomicInteger kbdzpEnergyRewardSum = new AtomicInteger(0);
        AtomicInteger kuaibiRewardSum = new AtomicInteger(0);
        invitationRepository.findDirectInvitationByInviterId(accountId).forEach(invitationRecord -> {
            int consumptionSum = (int) currencyChangeLogRepository.getEnergyConsumptionOfDate(invitationRecord.getAccountId(), yesterday);
            int kbdzpEnergyReward = (int) (consumptionSum * KbdzpConstants.INVITATION_REWARD_RATE);
            kbdzpEnergyReward = Math.max(InvitationConstants.REWARD_LOWER_LIMIT_EACH, kbdzpEnergyReward);

            int kuaibiConsuptionSum = (int) currencyChangeLogRepository.getKuaibiConsumptionOfDate(invitationRecord.getAccountId(), yesterday);
            int kuaibiConsuptionFromPlayerInteractive = (int) currencyChangeLogRepository.getKuaibiConsumptionOfDateFromPlayerInteractive(invitationRecord.getAccountId(), yesterday);
            int kuaibiReward = (int) ((kuaibiConsuptionSum - kuaibiConsuptionFromPlayerInteractive) * InvitationConstants.KUAIBI_INVITATION_REWARD_RATE
                    + kuaibiConsuptionFromPlayerInteractive * InvitationConstants.KUAIBI_INVITATION_REWARD_RATE_FROM_PLAYER_INTERACTIVE);

            InvitationRewardLog rewardLog = new InvitationRewardLog();
            rewardLog.setAccountId(accountId);
            rewardLog.setInviteeId(invitationRecord.getAccountId());
            rewardLog.setKbdzpEnergyReward(kbdzpEnergyReward);
            rewardLog.setKuaibiReward(kuaibiReward);
            rewardLog.setEventTime(new Date(timeProvider.currentTime()));
            invitationRewardLogRepository.save(rewardLog);

            kbdzpEnergyRewardSum.getAndAdd(kbdzpEnergyReward);
            kuaibiRewardSum.getAndAdd(kuaibiReward);
        });
        int kbdzpEnergyRewardSumFinal = Math.min(InvitationConstants.REWARD_UPPER_LIMIT_TOTAL, kbdzpEnergyRewardSum.get());
        int kuaibiRewardSumFinal = Math.min(InvitationConstants.REWARD_XS_UPPER_LIMIT_TOTAL, kuaibiRewardSum.get());
        
        record.setLastRewardResolveTime(new Date(timeProvider.currentTime()));
        record.setTodayKbdzpEnergyReward(kbdzpEnergyRewardSumFinal);
        record.setTodayKuaibiReward(kuaibiRewardSumFinal);
        record.setTodayRewardDelivered(false);

        return record;
    }

    public InviterRecord obtainInvitationReward(long accountId) {
        InviterRecord record = resolveInvitationReward(accountId);
        if (record.isTodayRewardDelivered()) {
            throw InvitationException.invitationRewardAlreadyDelivered();
        }

        kbdzpService.addExtraEnergy(accountId, record.getTodayKbdzpEnergyReward());
        currencyService.increaseCurrency(accountId, CurrencyConstants.ID_毫仙石, record.getTodayKuaibiReward(), CurrencyConstants.PURPOSE_INCREMENT_邀请回报);
        record.setTodayRewardDelivered(true);

        return record;
    }

    public InviterRecord extendInvitationLimit(long accountId) {
        InviterRecord inviterRecord = inviterRepository.findByIdForWrite(accountId).get();
        if (inviterRecord.getInvitationLimit() >= InvitationConstants.MAX_INVITATION_LIMIT) {
            throw InvitationException.invitationLimitReachMax();
        }
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, InvitationConstants.COST_EXTEND_INVITATION_LIMIT, true, CurrencyConstants.PURPOSE_DECREMENT_提升邀请上限);
        inviterRecord.increaseInvitationLimit();
        return inviterRecord;
    }
}
