/*
 * Created 2018-10-12 16:13:51
 */
package cn.com.yting.kxy.web.pet;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.util.CommaSeparatedLists;
import cn.com.yting.kxy.web.currency.CurrencyChangedEvent;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyRepository;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class PetGachaRankingService implements ResetTask, InitializingBean {

    @Autowired
    private PetGachaRankingRepository petGachaPointRankingRepository;
    @Autowired
    private PetGachaRankingAwardRepository petGachaPointRankingAwardRepository;
    @Autowired
    private PetGachaRankingSharedRepository petGachaRankingSharedRepository;
    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private PetService petService;
    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private TimeProvider timeProvider;

    @Override
    public void afterPropertiesSet() throws Exception {
        PetGachaRankingSharedRecord sharedRecord = new PetGachaRankingSharedRecord();
        sharedRecord.setRemainingYingting(PetConstants.INIT_YINGTING_AMOUNT);
        petGachaRankingSharedRepository.init(sharedRecord);
    }

    @EventListener
    public void onCurrencyChanged(CurrencyChangedEvent event) {
        if (event.getCurrencyId() == CurrencyConstants.ID_积分 && event.getAfterAmount() > event.getBeforeAmount()) {
            PetGachaRankingRecord rankingRecord = petGachaPointRankingRepository.findByIdForWrite(event.getAccountId());
            if (rankingRecord == null) {
                rankingRecord = new PetGachaRankingRecord();
                rankingRecord.setAccountId(event.getAccountId());
            }
            rankingRecord.setPoint(event.getAfterAmount());
            rankingRecord.setLastModified(new Date(timeProvider.currentTime()));
            petGachaPointRankingRepository.save(rankingRecord);
        }
    }

    @Override
    public void dailyReset() {
        List<PetGachaRankingRecord> rankingRecords = petGachaPointRankingRepository.findFirst100Ordered();

        currencyRepository.deleteByCurrencyId(CurrencyConstants.ID_积分);
        petGachaPointRankingAwardRepository.deleteAllInBatch();
        petGachaPointRankingRepository.deleteAllInBatch();

        int limit = 10;
        if (rankingRecords.size() < limit) {
            limit = rankingRecords.size();
        }
        for (int i = 0; i < limit; i++) {
            PetGachaRankingRecord rankingRecord = rankingRecords.get(i);
            PetGachaRankingAwardRecord awardRecord = new PetGachaRankingAwardRecord();
            awardRecord.setRanking(i + 1);
            awardRecord.setAccountId(rankingRecord.getAccountId());
            awardRecord.setFinalPoint(rankingRecord.getPoint());
            awardRecord.setAward(CommaSeparatedLists.toText(Arrays.asList("Currency", PetConstants.RANKING_AWARD_CURRENCY_ID, PetConstants.RANKING_AWARD_CURRENCY_AMOUNT)));
            petGachaPointRankingAwardRepository.save(awardRecord);
        }
    }

    public PetGachaRankingAwardResult obtainAward(long accountId) {
        PetGachaRankingAwardRecord awardRecord = petGachaPointRankingAwardRepository.findByAccountId(accountId);
        if (awardRecord == null || awardRecord.isDelivered()) {
            throw PetException.noAward();
        }

        PetGachaRankingAwardResult result;
        List<String> list = CommaSeparatedLists.fromText(awardRecord.getAward(), Function.identity());
        switch (list.get(0)) {
            case "Yingting":
                Pet pet = petService.createWithNumber(accountId, PetConstants.RANKING_AWARD_YINGTING_ID, Integer.parseInt(list.get(1)));
                result = new PetGachaRankingAwardResult(pet, null);
                break;
            case "Currency":
                CurrencyStack currencyStack = new CurrencyStack(Long.parseLong(list.get(1)), Long.parseLong(list.get(2)));
                currencyService.increaseCurrency(accountId, currencyStack.getCurrencyId(), currencyStack.getAmount());
                result = new PetGachaRankingAwardResult(null, currencyStack);
                break;
            default:
                throw new IllegalStateException("无法解析的奖励：" + awardRecord.getAward());
        }
        awardRecord.setDelivered(true);
        return result;
    }
}
