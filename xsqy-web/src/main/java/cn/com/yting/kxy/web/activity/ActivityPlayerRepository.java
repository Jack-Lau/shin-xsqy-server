/*
 * Created 2018-10-16 16:15:11
 */
package cn.com.yting.kxy.web.activity;

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
public interface ActivityPlayerRepository extends JpaRepository<ActivityPlayerRecord, Long> {

    @Query("SELECT r FROM ActivityPlayerRecord r WHERE r.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ActivityPlayerRecord> findByIdForWrite(long accountId);

    @Transactional
    default ActivityPlayerRecord findOrCreateById(long accountId) {
        return findByIdForWrite(accountId)
            .orElseGet(() -> {
                ActivityPlayerRecord record = new ActivityPlayerRecord();
                record.setAccountId(accountId);
                return save(record);
            });
    }

    @Modifying
    @Query("UPDATE ActivityPlayerRecord r SET r.incomingActivePoints = 0")
    void resetPoints();
}
