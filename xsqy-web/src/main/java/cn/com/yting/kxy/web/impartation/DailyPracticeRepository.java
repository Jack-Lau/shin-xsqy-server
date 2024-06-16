/*
 * Created 2018-11-21 17:57:36
 */
package cn.com.yting.kxy.web.impartation;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface DailyPracticeRepository extends JpaRepository<DailyPracticeRecord, DailyPracticeRecord.PK> {

    @Query("SELECT r FROM DailyPracticeRecord r WHERE r.accountId = ?1 AND r.definitionId = ?2")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DailyPracticeRecord> findByIdForWrite(long accountId, long definitionId);

    @Modifying
    @Query("UPDATE DailyPracticeRecord r SET r.status = cn.com.yting.kxy.web.impartation.DailyPracticeStatus.NOT_STARTED_YET, r.progress = 0 WHERE r.definitionId = ?1")
    void resetDailyPracticeStatusByDefinitionId(long definitionId);

    default DailyPracticeRecord findOrCreateRecord(long accountId, long definitionId) {
        return findByIdForWrite(accountId, definitionId).orElseGet(() -> {
            DailyPracticeRecord record = new DailyPracticeRecord();
            record.setAccountId(accountId);
            record.setDefinitionId(definitionId);
            record.setStatus(DailyPracticeStatus.NOT_STARTED_YET);
            return save(record);
        });
    }

    List<DailyPracticeRecord> findByAccountId(long accountId);

    List<DailyPracticeRecord> findByAccountIdAndStatus(long accountId, DailyPracticeStatus status);

    @Modifying
    void deleteByAccountId(long accountId);
}
