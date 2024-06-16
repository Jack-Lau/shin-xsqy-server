/*
 * Created 2018-11-2 12:52:59
 */
package cn.com.yting.kxy.web.awardpool;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface AwardPoolRepository extends JpaRepository<AwardPoolRecord, Long> {

    @Query("SELECT r FROM AwardPoolRecord r WHERE r.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AwardPoolRecord> findByIdForWrite(long poolId);
}
