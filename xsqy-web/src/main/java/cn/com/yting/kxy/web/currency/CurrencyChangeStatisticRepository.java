/*
 * Created 2018-11-21 13:04:54
 */
package cn.com.yting.kxy.web.currency;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import cn.com.yting.kxy.core.KxyConstants;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Azige
 */
public interface CurrencyChangeStatisticRepository extends JpaRepository<CurrencyChangeStatistic, CurrencyChangeStatistic.PK> {

    default Optional<CurrencyChangeStatistic> findById(LocalDate statisticDate, long currencyId) {
        return findById(new CurrencyChangeStatistic.PK(Date.from(statisticDate.atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET)), currencyId));
    }
}
