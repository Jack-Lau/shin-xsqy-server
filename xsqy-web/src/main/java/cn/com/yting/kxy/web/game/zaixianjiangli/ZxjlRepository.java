/*
 * Created 2019-1-23 15:18:27
 */
package cn.com.yting.kxy.web.game.zaixianjiangli;

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
public interface ZxjlRepository extends JpaRepository<ZxjlRecord, Long> {

    @Query("FROM ZxjlRecord r WHERE r.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ZxjlRecord> findByIdForWrite(long accountId);

    @Modifying
    @Query("UPDATE ZxjlRecord r SET r.award_1_delivered = FALSE, r.award_2_delivered = FALSE, r.award_3_delivered = FALSE, r.award_4_delivered = FALSE, r.award_5_delivered = FALSE")
    void resetAwardDelivered();
}
