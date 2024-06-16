/*
 * Created 2019-2-13 16:58:17
 */
package cn.com.yting.kxy.web.game.yuanxiaojiayao;

import java.util.Date;
import java.util.Objects;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.scheduling.RegisterScheduledTask;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.activity.ActivityService;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.google.common.collect.ImmutableMap;
import java.time.DayOfWeek;
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
public class YxjyService implements InitializingBean, ResetTask {

    @Autowired
    private YxjyRepository yxjyRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MailService mailService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ChatService chatService;

    @Autowired
    private TimeProvider timeProvider;

    private boolean available = false;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (timeProvider.today().getDayOfWeek() == DayOfWeek.SATURDAY || timeProvider.today().getDayOfWeek() == DayOfWeek.SUNDAY) {
            if (!available) {
                open();
            }
        } else {
            if (available) {
                close();
            }
        }
    }

    public YxjyRecord createRecord(long accountId) {
        if (yxjyRepository.existsById(accountId)) {
            throw KxyWebException.unknown("记录已经存在");
        }
        if (playerRepository.findById(accountId).get().getPlayerLevel() < 50) {
            throw KxyWebException.unknown("等级不足");
        }

        YxjyRecord record = new YxjyRecord();
        record.setAccountId(accountId);
        return yxjyRepository.save(record);
    }

    public YxjyRecord publishInvitation(long accountId) {
        YxjyRecord record = yxjyRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.notFound("记录不存在"));
        if (available) {
            Date currentTime = new Date(timeProvider.currentTime());
            if (record.getLastInvitationTime() != null && currentTime.getTime() - record.getLastInvitationTime().getTime() < 180_000) {
                throw new YxjyException(YxjyException.EC_邀请时间间隔未到, "邀请时间间隔未到");
            }

            record.setLastInvitationTime(currentTime);
            Player player = playerRepository.findById(accountId).get();
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(3200054, ImmutableMap.of("playerName", player.getPlayerName(), "accountId", accountId))
            );
        }
        return record;
    }

    public YxjyRecord attend(long accountId, long targetAccountId) {
        if (accountId == targetAccountId) {
            throw KxyWebException.unknown("不能参加自己的邀请");
        }
        YxjyRecord record = yxjyRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.notFound("记录不存在"));
        if (record.getTodayAttendedCount() >= 5) {
            throw KxyWebException.unknown("今日赴宴已达上限");
        }
        if (record.getAttendedAccountIds().contains(targetAccountId)) {
            throw new YxjyException(YxjyException.EC_已经参加过指定玩家的邀请, "已经参加过指定玩家的邀请");
        }
        YxjyRecord targetRecord = yxjyRepository.findByIdForWrite(targetAccountId).orElseThrow(() -> KxyWebException.notFound("目标记录不存在"));
        if (targetRecord.getInvitedAccountIds().stream().filter(Objects::nonNull).count() >= 3) {
            throw new YxjyException(YxjyException.EC_对方的邀请数量已满, "对方的邀请数量已满");
        }

        if (available) {
            currencyService.increaseCurrency(accountId, 20054, 1);
            record.increaseTodayAttendedCount();
            for (int i = 0; i < record.getAttendedAccountIds().size(); i++) {
                if (record.getAttendedAccountIds().get(i) == null) {
                    record.getAttendedAccountIds().set(i, targetAccountId);
                    break;
                }
            }
            for (int i = 0; i < targetRecord.getInvitedAccountIds().size(); i++) {
                if (targetRecord.getInvitedAccountIds().get(i) == null) {
                    targetRecord.getInvitedAccountIds().set(i, accountId);
                    break;
                }
            }
            activityService.makeProgress(targetAccountId, 158005, 3);

            if (targetRecord.getInvitedAccountIds().stream().filter(Objects::nonNull).count() == 3 && targetRecord.getAwardSatatus().equals(YxjyAwardSatatus.NOT_AVAILABLE)) {
                targetRecord.setAwardSatatus(YxjyAwardSatatus.AVAILABLE);
            }
        }

        return record;
    }

    public YxjyRecord obtainAward(long accountId) {
        YxjyRecord record = yxjyRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.notFound("记录不存在"));
        if (record.getInvitedAccountIds().stream().filter(Objects::nonNull).count() != 3) {
            throw KxyWebException.unknown("邀请人数不足3人");
        }
        if (!record.getAwardSatatus().equals(YxjyAwardSatatus.AVAILABLE)) {
            throw KxyWebException.unknown("已经领取过");
        }

        if (available) {
            currencyService.increaseCurrency(accountId, 20053, 1);
            record.setAwardSatatus(YxjyAwardSatatus.DELIVERED);
        }

        return record;
    }

    @Override
    public void dailyReset() {
        if (available) {
            balance();
        }
    }

    @RegisterScheduledTask(cronExpression = "0 1 0 ? * SAT", executeIfNew = true)
    public void open() {
        available = true;
    }

    @RegisterScheduledTask(cronExpression = "0 59 23 ? * SUN", executeIfNew = true)
    public void close() {
        if (available) {
            dailyReset();
        }
        available = false;
    }

    public boolean isAvailable() {
        return available;
    }

    public void balance() {
        yxjyRepository.findAll().forEach(record -> {
            if (record.getAwardSatatus().equals(YxjyAwardSatatus.AVAILABLE)) {
                new MailSendingRequest()
                        .to(record.getAccountId())
                        .template(64)
                        .attachment("20053:1")
                        .commit(mailService);
            }
            record.setAwardSatatus(YxjyAwardSatatus.NOT_AVAILABLE);
            record.setLastInvitationTime(null);
            for (int i = 0; i < record.getInvitedAccountIds().size(); i++) {
                record.getInvitedAccountIds().set(i, null);
            }
            record.setTodayAttendedCount(0);
            for (int i = 0; i < record.getAttendedAccountIds().size(); i++) {
                record.getAttendedAccountIds().set(i, null);
            }
        });
    }

}
