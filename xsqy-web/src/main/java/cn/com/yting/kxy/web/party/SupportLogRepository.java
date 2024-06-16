/*
 * Created 2018-9-27 18:14:09
 */
package cn.com.yting.kxy.web.party;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import cn.com.yting.kxy.core.KxyConstants;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface SupportLogRepository extends JpaRepository<SupportLog, Long> {

    @Query("SELECT COALESCE(SUM(l.fee), 0)"
        + " FROM SupportLog l"
        + " WHERE l.supporterAccountId = ?1"
        + " AND l.eventTime >= ?2"
        + " AND l.eventTime < ?3"
    )
    long sumFeeBySupportAccountIdAndEventTimeRange(long supportAccountId, Date startEventTime, Date endEventTime);

    default long getFeeSumOfDate(long supportAccountId, LocalDate date) {
        Date startEventTime = Date.from(date.atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        Date endEventTime = Date.from(date.plusDays(1).atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        return sumFeeBySupportAccountIdAndEventTimeRange(supportAccountId, startEventTime, endEventTime);
    }

    @Query("SELECT l FROM SupportLog l WHERE l.supporterAccountId = ?1 ORDER BY l.eventTime DESC")
    List<SupportLog> findBySupportAccountIdOrdered(long supportAccountId, Pageable pageable);
}
