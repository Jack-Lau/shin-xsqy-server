/*
 * Created 2018-6-27 15:41:28
 */
package cn.com.yting.kxy.web.currency;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import cn.com.yting.kxy.core.KxyConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface CurrencyChangeLogRepository extends JpaRepository<CurrencyChangeLog, Long> {

    @Query("SELECT COALESCE(SUM(c.beforeAmount - c.afterAmount), 0)"
            + " FROM CurrencyChangeLog c"
            + " WHERE c.accountId = ?1"
            + " AND c.currencyId = ?2"
            + " AND c.eventTime >= ?3"
            + " AND c.eventTime < ?4"
            + " AND c.afterAmount < c.beforeAmount"
    )
    long sumConsumptionByAccountIdAndEventTimeRange(long accountId, long currencyId, Date startEventTime, Date endEventTime);

    default long getEnergyConsumptionOfDate(long accountId, LocalDate date) {
        Date startEventTime = Date.from(date.atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        Date endEventTime = Date.from(date.plusDays(1).atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        return sumConsumptionByAccountIdAndEventTimeRange(accountId, CurrencyConstants.ID_能量, startEventTime, endEventTime);
    }

    @Query("SELECT COALESCE(SUM(c.beforeAmount - c.afterAmount), 0)"
            + " FROM CurrencyChangeLog c"
            + " WHERE c.accountId = ?1"
            + " AND c.currencyId = ?2"
            + " AND c.eventTime >= ?3"
            + " AND c.eventTime < ?4"
            + " AND c.afterAmount < c.beforeAmount"
            + " AND c.purpose NOT IN (?5)"
    )
    long sumConsumptionByAccountIdAndEventTimeRangeAndPurposeExcluded(long accountId, long currencyId, Date startEventTime, Date endEventTime, List<Integer> excludedPurposes);

    @Query("SELECT COALESCE(SUM(c.beforeAmount - c.afterAmount), 0)"
            + " FROM CurrencyChangeLog c"
            + " WHERE c.accountId = ?1"
            + " AND c.currencyId = ?2"
            + " AND c.eventTime >= ?3"
            + " AND c.eventTime < ?4"
            + " AND c.afterAmount < c.beforeAmount"
            + " AND c.purpose IN (?5)"
    )
    long sumConsumptionByAccountIdAndEventTimeRangeAndPurposeIncluded(long accountId, long currencyId, Date startEventTime, Date endEventTime, List<Integer> includedPurposes);

    default long getKuaibiConsumptionOfDate(long accountId, LocalDate date) {
        Date startEventTime = Date.from(date.atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        Date endEventTime = Date.from(date.plusDays(1).atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        return sumConsumptionByAccountIdAndEventTimeRangeAndPurposeExcluded(accountId, CurrencyConstants.ID_毫仙石, startEventTime, endEventTime, CurrencyConstants.PURPOSE_DECREMENT_FROM_TRANSFER);
    }

    default long getKuaibiConsumptionOfDateFromPlayerInteractive(long accountId, LocalDate date) {
        Date startEventTime = Date.from(date.atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        Date endEventTime = Date.from(date.plusDays(1).atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        return sumConsumptionByAccountIdAndEventTimeRangeAndPurposeIncluded(accountId, CurrencyConstants.ID_毫仙石, startEventTime, endEventTime, CurrencyConstants.PURPOSE_DECREMENT_FROM_PLAYER_INTERACTIVE);
    }

    @Query("SELECT l"
        + " FROM CurrencyChangeLog l"
        + " WHERE l.currencyId = ?1"
        + " AND l.eventTime >= ?2"
        + " AND l.eventTime < ?3")
    List<CurrencyChangeLog> findByCurrencyIdAndEventTimeRange(long currencyId, Date startEventTime, Date endEventTime);

    default List<CurrencyChangeLog> findByCurrencyIdOfDate(long currencyId, LocalDate date) {
        Date startEventTime = Date.from(date.atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        Date endEventTime = Date.from(date.plusDays(1).atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        return findByCurrencyIdAndEventTimeRange(currencyId, startEventTime, endEventTime);
    }

    List<CurrencyChangeLog> findByAccountIdAndCurrencyIdAndPurpose(long accountId, long currencyId, int purpose);

    long countByAccountIdAndPurpose(long accountId, int purpose);

    long countByAccountIdAndCurrencyIdAndPurpose(long accountId, long currencyId, int purpose);

    @Query("SELECT COALESCE(SUM(c.beforeAmount - c.afterAmount), 0)"
        + " FROM CurrencyChangeLog c"
        + " WHERE c.accountId = ?1"
        + " AND c.currencyId = ?2"
        + " AND c.afterAmount < c.beforeAmount")
    long sumConsumptionByAccountIdAndCurrencyId(long accountId, long currencyId);
}
