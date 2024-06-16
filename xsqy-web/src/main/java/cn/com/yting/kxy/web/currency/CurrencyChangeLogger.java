/*
 * Created 2018-6-27 15:37:32
 */
package cn.com.yting.kxy.web.currency;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Component
@Transactional
public class CurrencyChangeLogger {

    @Autowired
    private CurrencyChangeLogRepository currencyChangeLogRepository;

    @EventListener
    public void onCurrencyChanged(CurrencyChangedEvent event) {
        CurrencyChangeLog log = new CurrencyChangeLog();
        log.setAccountId(event.getAccountId());
        log.setCurrencyId(event.getCurrencyId());
        log.setBeforeAmount(event.getBeforeAmount());
        log.setAfterAmount(event.getAfterAmount());
        log.setEventTime(new Date(event.getTimestamp()));
        log.setPurpose(event.getPurpose());

        currencyChangeLogRepository.save(log);
    }
}
