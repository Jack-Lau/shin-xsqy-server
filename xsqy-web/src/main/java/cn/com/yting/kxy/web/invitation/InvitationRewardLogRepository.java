/*
 * Created 2018-7-10 12:52:39
 */
package cn.com.yting.kxy.web.invitation;

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
public interface InvitationRewardLogRepository extends JpaRepository<InvitationRewardLog, Long>{

    @Query("SELECT k"
        + " FROM InvitationRewardLog k"
        + " WHERE k.accountId = ?1"
        + " AND k.eventTime >= ?2"
        + " AND k.eventTime < ?3"
    )
    List<InvitationRewardLog> findByAccountIdAndEventTimeRange(long accountId, Date startEventTime, Date endEventTime);

    default List<InvitationRewardLog> findInDate(long accountId, LocalDate date) {
        Date startEventTime = Date.from(date.atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        Date endEventTime = Date.from(date.plusDays(1).atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        return findByAccountIdAndEventTimeRange(accountId, startEventTime, endEventTime);
    }
}
