/*
 * Created 2018-9-19 15:14:24
 */
package cn.com.yting.kxy.web.price;

import java.time.OffsetDateTime;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyRecord;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiUnits;
import cn.com.yting.kxy.web.price.resource.FloatingPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class PriceService {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void initRecord(FloatingPrice floatingPrice) {
        if (!priceRepository.existsById(floatingPrice.getId())) {
            PriceRecord record = new PriceRecord();
            record.setId(floatingPrice.getId());
            record.setCurrentPrice(floatingPrice.getStart());
            priceRepository.save(record);
        }
    }

    public void reducePrice(FloatingPrice floatingPrice) {
        OffsetDateTime currentTime = timeProvider.currentOffsetDateTime();
        if (currentTime.getHour() >= 2 && currentTime.getHour() < 8) {
            return;
        }

        PriceRecord record = priceRepository.findByIdForWrite(floatingPrice.getId()).get();
        if (record.getCurrentPrice() > floatingPrice.getMin()) {
            long newPrice = (long) (record.getCurrentPrice() * (1 - floatingPrice.getReduceValue()));
            if (floatingPrice.getCurrency() == CurrencyConstants.ID_毫仙石) {
                newPrice = KuaibiUnits.truncateAtKuaibi(newPrice);
            }
            if (newPrice < floatingPrice.getMin()) {
                newPrice = floatingPrice.getMin();
            }
            record.setCurrentPrice(newPrice);
            eventPublisher.publishEvent(new PriceReduceEvent(this, record));
        }
    }

    public long getCurrentPrice(long priceId) {
        return priceRepository.findById(priceId).get().getCurrentPrice();
    }

    public void deduct(long accountId, long priceId, long expectedPrice, Integer purpose) {
        deduct(accountId, priceId, expectedPrice, purpose, 1, 1);
    }

    public void deduct(long accountId, long priceId, long expectedPrice, Integer purpose, int usedCountIncrement, double priceMultipier) {
        deduct(accountId, priceId, expectedPrice, purpose, usedCountIncrement, priceMultipier, 0);
    }

    public void deduct(long accountId, long priceId, long expectedPrice, Integer purpose, int usedCountIncrement, double priceMultipier, long activePointsToUse) {
        PriceRecord record = priceRepository.findByIdForWrite(priceId).get();
        FloatingPrice floatingPrice = resourceContext.getLoader(FloatingPrice.class).get(priceId);
        if (record.getCurrentPrice() != expectedPrice) {
            throw PriceException.expectedPriceNotMatch();
        }
        long currencyToUse = (long) (record.getCurrentPrice() * priceMultipier);
        //
        if (floatingPrice.getCurrency() == CurrencyConstants.ID_元宝) {
            CurrencyRecord activePointsRecord = currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_活跃点);
            if (activePointsRecord.getAmount() < activePointsToUse) {
                throw PriceException.insufficientActivePoint();
            }
            activePointsToUse = (long) Math.min(currencyToUse / 2 * PriceConstants.RATE_PRICE_TO_活跃度, activePointsToUse);
            currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_活跃点, activePointsToUse, true, purpose);
            currencyToUse -= activePointsToUse / PriceConstants.RATE_PRICE_TO_活跃度;
            if (currencyToUse < 0) {
                currencyToUse = 0;
            }
        }
        //
        currencyService.decreaseCurrency(accountId, floatingPrice.getCurrency(), currencyToUse, true, purpose);
        record.increaseUsedCount(usedCountIncrement);
        if (record.getUsedCount() >= floatingPrice.getCromoteCondition()) {
            record.setCurrentPrice(record.getCurrentPrice() + floatingPrice.getCromoteValue());
            record.setUsedCount(0);
        }
    }

}
