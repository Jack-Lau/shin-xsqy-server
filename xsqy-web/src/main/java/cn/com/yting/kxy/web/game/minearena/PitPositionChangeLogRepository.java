/*
 * Created 2018-10-17 17:41:24
 */
package cn.com.yting.kxy.web.game.minearena;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import cn.com.yting.kxy.core.KxyConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface PitPositionChangeLogRepository extends JpaRepository<PitPositionChangeLog, Long> {

    @Query("SELECT l FROM PitPositionChangeLog l"
        + " WHERE l.accountId = ?1"
        + " AND l.eventTime >= ?2"
        + " AND l.eventTime < ?3"
        + " ORDER BY l.eventTime")
    List<PitPositionChangeLog> findByAccountIdAndTimeRange(long accountId, Date startTime, Date endTime);

    default List<PitPositionChangeLog> findByAccountIdAndDate(long accountId, LocalDate date) {
        Date startEventTime = Date.from(date.atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        Date endEventTime = Date.from(date.plusDays(1).atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        return findByAccountIdAndTimeRange(accountId, startEventTime, endEventTime);
    }

    List<PitPositionChangeLog> findByAccountIdOrderByEventTimeDesc(long accountId, Pageable pageable);

    @Query("SELECT l FROM PitPositionChangeLog l"
        + " WHERE l.accountId = ?1"
        + " AND l.eventTime < ?2"
        + " ORDER BY l.eventTime DESC")
    List<PitPositionChangeLog> findByAccountIdBeforeTimeOrderByEventTimeDesc(long accountId, Date seekTime, Pageable pageable);

    default PitPositionChangeLog findLatestByAccountIdBeforeDate(long accountId, LocalDate seekDate) {
        Date seekTime = Date.from(seekDate.atStartOfDay().atOffset(KxyConstants.KXY_TIME_OFFSET).toInstant());
        List<PitPositionChangeLog> list = findByAccountIdBeforeTimeOrderByEventTimeDesc(accountId, seekTime, PageRequest.of(0, 1));
        if (list.size() >= 1) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
