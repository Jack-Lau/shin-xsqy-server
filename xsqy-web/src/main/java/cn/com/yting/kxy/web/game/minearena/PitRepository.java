/*
 * Created 2018-10-17 16:40:30
 */
package cn.com.yting.kxy.web.game.minearena;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface PitRepository extends JpaRepository<Pit, Long> {

    @Query("SELECT p FROM Pit p WHERE p.position = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Pit> findByIdForWrite(long position);

    Optional<Pit> findByAccountId(long accountId);

    @Query("SELECT p FROM Pit p WHERE p.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Pit> findByAccountIdForWrite(long accountId);

    @Query("SELECT p FROM Pit p ORDER BY p.position ASC")
    List<Pit> findOrderByPosition(Pageable pageable);

    @Modifying
    @Query("UPDATE Pit p SET p.challengedCount = 0")
    void resetChallengedCount();
}
