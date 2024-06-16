/*
 * Created 2018-6-26 17:36:29
 */
package cn.com.yting.kxy.web.currency;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import cn.com.yting.kxy.core.KxyConstants;
import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.util.TimeUtils;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiService;
import cn.com.yting.kxy.web.currency.resource.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class CurrencyService {

    @Autowired
    private KuaibiService kuaibiService;

    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private CurrencyChangeLogRepository currencyChangeLogRepository;
    @Autowired
    private CurrencyChangeStatisticRepository currencyChangeStatisticRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private TimeProvider timeProvider;

    public long getCurrencyAmount(long accountId, long currencyId) {
        return findOrDummyRecord(accountId, currencyId).getAmount();
    }

    public void increaseCurrency(long accountId, long currencyId, long amount) {
        increaseCurrency(accountId, currencyId, amount, null);
    }

    public void increaseCurrency(long accountId, long currencyId, long amount, Integer purpose) {
        long acturalAmount = amount;
        if (acturalAmount < 0) {
            throw new IllegalArgumentException("增加的货币数必须非负，amount=" + acturalAmount);
        } else if (acturalAmount == 0) {
            return;
        }
        //
        if (currencyId == CurrencyConstants.ID_毫仙石) {
            if (purpose == null) {
                purpose = CurrencyConstants.PURPOSE_INCREMENT_未指定块币产出源;
            }
            if (CurrencyConstants.PURPOSE_INCREMENT_FROM_AIRDROP.contains(purpose)) {
                acturalAmount = kuaibiService.withdrawMilliKuaibiFromAirdrop(amount);
            } else if (CurrencyConstants.PURPOSE_INCREMENT_FROM_REBATE.contains(purpose)) {
                acturalAmount = kuaibiService.withdrawMilliKuaibiFromRebate(amount);
            } else if (CurrencyConstants.PURPOSE_INCREMENT_FROM_TRANSFER.contains(purpose)) {
                acturalAmount = amount;
            }
        }
        //
        CurrencyRecord record = findOrCreateRecord(accountId, currencyId);
        CurrencyDefinition currencyDefinition = getCurrencyDefinition(currencyId);
        long beforeAmount = record.getAmount();
        long afterAmount = beforeAmount + acturalAmount;
        // 处理溢出
        if (afterAmount > currencyDefinition.getMaxAmount() || afterAmount < beforeAmount) {
            afterAmount = currencyDefinition.getMaxAmount();
        }
        record.setAmount(afterAmount);

        fireCurrencyChangedEvent(accountId, currencyId, beforeAmount, afterAmount, purpose);
    }

    public void decreaseCurrency(long accountId, long currencyId, long amount) {
        decreaseCurrency(accountId, currencyId, amount, true);
    }

    public void decreaseCurrency(long accountId, long currencyId, long amount, boolean failIfInsufficient) {
        decreaseCurrency(accountId, currencyId, amount, failIfInsufficient, null);
    }

    public void decreaseCurrency(long accountId, long currencyId, long amount, boolean failIfInsufficient, Integer purpose) {
        if (amount < 0) {
            throw new IllegalArgumentException("减少的货币数必须非负，amount=" + amount);
        } else if (amount == 0) {
            return;
        }

        CurrencyRecord record = findOrCreateRecord(accountId, currencyId);
        long beforeAmount = record.getAmount();
        long afterAmount = beforeAmount - amount;
        // 处理溢出
        if (afterAmount < 0 || afterAmount > beforeAmount) {
            if (failIfInsufficient) {
                throw KxyWebException.unknown("货币不足以减少");
            } else {
                afterAmount = 0;
            }
        }
        record.setAmount(afterAmount);

        fireCurrencyChangedEvent(accountId, currencyId, beforeAmount, afterAmount, purpose);
    }

    private void fireCurrencyChangedEvent(long accountId, long currencyId, long beforeAmount, long afterAmount, Integer purpose) {
        CurrencyChangedEvent event = new CurrencyChangedEvent(this, accountId, currencyId, beforeAmount, afterAmount, purpose);
        eventPublisher.publishEvent(event);
    }

    private CurrencyDefinition getCurrencyDefinition(long currencyId) {
        return Currency.getFrom(resourceContext, currencyId);
    }

    @Transactional(readOnly = true)
    public CurrencyRecord findOrDummyRecord(long accountId, long currencyId) {
        CurrencyRecord record = currencyRepository.findById(accountId, currencyId).orElse(null);
        if (record == null) {
            record = new CurrencyRecord();
            record.setAccountId(accountId);
            record.setCurrencyId(currencyId);
        }
        return record;
    }

    public CurrencyRecord findOrCreateRecord(long accountId, long currencyId) {
        CurrencyRecord record = currencyRepository.findByIdForWrite(accountId, currencyId).orElse(null);
        if (record == null) {
            record = new CurrencyRecord();
            record.setAccountId(accountId);
            record.setCurrencyId(currencyId);
            record = currencyRepository.saveAndFlush(record);
        }
        return record;
    }

    public CurrencyChangeStatistic findOrGatherCurrencyStatistic(long currencyId, LocalDate date) {
        CurrencyChangeStatistic currencyChangeStatistic = currencyChangeStatisticRepository.findById(date, currencyId).orElse(null);
        boolean needGather = false;
        if (currencyChangeStatistic == null) {
            currencyChangeStatistic = new CurrencyChangeStatistic();
            needGather = true;
        } else {
            LocalDate lastModifiedDate = TimeUtils.toOffsetTime(currencyChangeStatistic.getLastModified()).toLocalDate();
            if (!lastModifiedDate.isAfter(date) && lastModifiedDate.isBefore(timeProvider.today())) {
                needGather = true;
            }
        }
        if (needGather) {
            List<CurrencyChangeLog> logs = currencyChangeLogRepository.findByCurrencyIdOfDate(currencyId, date);
            currencyChangeStatistic.setStatisticDate(Date.from(date.atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET)));
            currencyChangeStatistic.setCurrencyId(currencyId);
            currencyChangeStatistic.setTotalGain(logs.stream()
                    .mapToLong(it -> it.getAfterAmount() - it.getBeforeAmount())
                    .filter(it -> it > 0)
                    .sum());
            currencyChangeStatistic.setTotalDrain(logs.stream()
                    .mapToLong(it -> it.getBeforeAmount() - it.getAfterAmount())
                    .filter(it -> it > 0)
                    .sum());
            currencyChangeStatistic.setLastModified(new Date(timeProvider.currentTime()));
            currencyChangeStatistic = currencyChangeStatisticRepository.save(currencyChangeStatistic);
        }
        return currencyChangeStatistic;
    }
}
