/*
 * Created 2018-10-16 16:00:38
 */
package cn.com.yting.kxy.web.activity;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
public interface ActivityRepository extends JpaRepository<ActivityRecord, ActivityRecord.PK> {

    @Query("SELECT r FROM ActivityRecord r WHERE r.accountId = ?1 AND r.activityId = ?2")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ActivityRecord> findByIdForWrite(long accountId, long activityId);

    @Transactional
    default ActivityRecord findOrCreateById(long accountId, long activityId) {
        return findByIdForWrite(accountId, activityId)
            .orElseGet(() -> {
                ActivityRecord record = new ActivityRecord();
                record.setAccountId(accountId);
                record.setActivityId(activityId);
                return save(record);
            });
    }

    List<ActivityRecord> findByAccountId(long accountId);

    @Modifying
    @Query("UPDATE ActivityRecord r SET r.progress = 0, r.completed = false WHERE r.activityId = ?1")
    void resetProgressByActivityId(long activityId);
}
