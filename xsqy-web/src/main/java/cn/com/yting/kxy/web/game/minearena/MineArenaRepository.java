/*
 * Created 2018-10-17 18:48:02
 */
package cn.com.yting.kxy.web.game.minearena;

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
public interface MineArenaRepository extends JpaRepository<MineArenaRecord, Long> {

    @Query("SELECT r FROM MineArenaRecord r WHERE r.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<MineArenaRecord> findByIdForWrite(long accountId);

    @Modifying
    @Query("UPDATE MineArenaRecord r SET r.challengePoint = ?1")
    void resetChallengePoint(int value);
}
