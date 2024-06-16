/*
 * Created 2019-1-23 15:18:16
 */
package cn.com.yting.kxy.web.game.zaixianjiangli;

import cn.com.yting.kxy.core.TimeProvider;
import java.util.ArrayList;
import java.util.List;

import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.scheduling.RegisterScheduledTask;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.activity.ActivityService;
import cn.com.yting.kxy.web.award.AwardResult;
import cn.com.yting.kxy.web.award.AwardService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.game.zaixianjiangli.resource.GameOnlineTime;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
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
public class ZxjlService implements InitializingBean, ResetTask {

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ZxjlRepository zxjlRepository;

    @Autowired
    private AwardService awardService;
    @Autowired
    private MailService mailService;
    @Autowired
    private ActivityService activityService;

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ResourceContext resourceContext;

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

    public ZxjlRecord findOrCreateRecord(long accountId) {
        ZxjlRecord record = zxjlRepository.findById(accountId).orElse(null);
        if (record == null) {
            Player player = playerRepository.findById(accountId).get();
            if (player.getPlayerLevel() < 50) {
                return null;
            }
            record = new ZxjlRecord();
            record.setAccountId(accountId);
            record = zxjlRepository.save(record);
        }
        return record;
    }

    public AwardResult obtainAward(long accountId, int index) {
        ZxjlRecord record = zxjlRepository.findByIdForWrite(accountId).get();
        AwardResult result = null;
        if (available) {
            switch (index) {
                case 0:
                    if (record.isAward_1_delivered()) {
                        throw KxyWebException.unknown("已经领取过");
                    }
                    result = doObtainAward(accountId, resourceContext.getLoader(GameOnlineTime.class).get(120401));
                    record.setAward_1_delivered(true);
                    break;
                case 1:
                    if (record.isAward_2_delivered()) {
                        throw KxyWebException.unknown("已经领取过");
                    }
                    result = doObtainAward(accountId, resourceContext.getLoader(GameOnlineTime.class).get(120402));
                    record.setAward_2_delivered(true);
                    break;
                case 2:
                    if (record.isAward_3_delivered()) {
                        throw KxyWebException.unknown("已经领取过");
                    }
                    result = doObtainAward(accountId, resourceContext.getLoader(GameOnlineTime.class).get(120403));
                    record.setAward_3_delivered(true);
                    break;
                case 3:
                    if (record.isAward_4_delivered()) {
                        throw KxyWebException.unknown("已经领取过");
                    }
                    result = doObtainAward(accountId, resourceContext.getLoader(GameOnlineTime.class).get(120404));
                    record.setAward_4_delivered(true);
                    break;
                case 4:
                    if (record.isAward_5_delivered()) {
                        throw KxyWebException.unknown("已经领取过");
                    }
                    result = doObtainAward(accountId, resourceContext.getLoader(GameOnlineTime.class).get(120405));
                    record.setAward_5_delivered(true);
                    break;
            }
        }
        return result;
    }

    private AwardResult doObtainAward(long accountId, GameOnlineTime gameOnlineTime) {
        Player player = playerRepository.findById(accountId).get();
        if (player.getOnlineTimeCount() < gameOnlineTime.getTime() * 1000) {
            throw KxyWebException.unknown("在线时间不够");
        }
        activityService.makeProgress(accountId, 158006, 5);
        return awardService.processAward(accountId, gameOnlineTime.getAward());
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
        zxjlRepository.findAll().forEach(record -> {
            Player player = playerRepository.findById(record.getAccountId()).orElse(null);
            if (player != null) {
                List<CurrencyStack> currencyStacks = new ArrayList<>();
                {
                    GameOnlineTime gameOnlineTime = resourceContext.getLoader(GameOnlineTime.class).get(120401);
                    if (player.getOnlineTimeCount() >= gameOnlineTime.getTime() * 1000 && !record.isAward_1_delivered()) {
                        currencyStacks.add(new CurrencyStack(gameOnlineTime.getCurrency().getId(), gameOnlineTime.getCurrency().getAmount()));
                    }
                }
                {
                    GameOnlineTime gameOnlineTime = resourceContext.getLoader(GameOnlineTime.class).get(120402);
                    if (player.getOnlineTimeCount() >= gameOnlineTime.getTime() * 1000 && !record.isAward_2_delivered()) {
                        currencyStacks.add(new CurrencyStack(gameOnlineTime.getCurrency().getId(), gameOnlineTime.getCurrency().getAmount()));
                    }
                }
                {
                    GameOnlineTime gameOnlineTime = resourceContext.getLoader(GameOnlineTime.class).get(120403);
                    if (player.getOnlineTimeCount() >= gameOnlineTime.getTime() * 1000 && !record.isAward_3_delivered()) {
                        currencyStacks.add(new CurrencyStack(gameOnlineTime.getCurrency().getId(), gameOnlineTime.getCurrency().getAmount()));
                    }
                }
                {
                    GameOnlineTime gameOnlineTime = resourceContext.getLoader(GameOnlineTime.class).get(120404);
                    if (player.getOnlineTimeCount() >= gameOnlineTime.getTime() * 1000 && !record.isAward_4_delivered()) {
                        currencyStacks.add(new CurrencyStack(gameOnlineTime.getCurrency().getId(), gameOnlineTime.getCurrency().getAmount()));
                    }
                }
                {
                    GameOnlineTime gameOnlineTime = resourceContext.getLoader(GameOnlineTime.class).get(120405);
                    if (player.getOnlineTimeCount() >= gameOnlineTime.getTime() * 1000 && !record.isAward_5_delivered()) {
                        currencyStacks.add(new CurrencyStack(gameOnlineTime.getCurrency().getId(), gameOnlineTime.getCurrency().getAmount()));
                    }
                }
                if (!currencyStacks.isEmpty()) {
                    new MailSendingRequest()
                            .to(record.getAccountId())
                            .template(61)
                            .attachment(currencyStacks)
                            .commit(mailService);
                }
            }
            record.setAward_1_delivered(false);
            record.setAward_2_delivered(false);
            record.setAward_3_delivered(false);
            record.setAward_4_delivered(false);
            record.setAward_5_delivered(false);
        });
    }

}
