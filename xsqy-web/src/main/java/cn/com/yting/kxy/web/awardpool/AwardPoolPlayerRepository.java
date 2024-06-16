/*
 * Created 2018-11-2 15:18:00
 */
package cn.com.yting.kxy.web.awardpool;

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
public interface AwardPoolPlayerRepository extends JpaRepository<AwardPoolPlayerRecord, AwardPoolPlayerRecord.PK> {

    @Query("SELECT r FROM AwardPoolPlayerRecord r WHERE r.poolId = ?1 AND r.accountId= ?2")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AwardPoolPlayerRecord> findByIdForWrite(long poolId, long accountId);

    default AwardPoolPlayerRecord findOrCreateById(long poolId, long accountId, long initPoolValue) {
        return findByIdForWrite(poolId, accountId).orElseGet(() -> {
            AwardPoolPlayerRecord record = new AwardPoolPlayerRecord();
            record.setPoolId(poolId);
            record.setAccountId(accountId);
            record.setPoolValue(initPoolValue);
            return save(record);
        });
    }

    @Modifying
    @Query("UPDATE AwardPoolPlayerRecord r SET r.poolValue = ?2 WHERE r.poolId = ?1")
    void resetPoolValueByPoolId(long poolId, long poolValue);
}
