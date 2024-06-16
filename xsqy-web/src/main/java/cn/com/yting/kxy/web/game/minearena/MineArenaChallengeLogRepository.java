/*
 * Created 2018-10-20 17:41:29
 */
package cn.com.yting.kxy.web.game.minearena;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface MineArenaChallengeLogRepository extends JpaRepository<MineArenaChallengeLog, Long> {

    @Query("SELECT l FROM MineArenaChallengeLog l"
        + " WHERE l.challengerAccountId = ?1"
        + " OR l.defenderAccountId = ?1"
        + " ORDER BY l.eventTime DESC")
    List<MineArenaChallengeLog> findByAccountIdOrderByEventTimeDesc(long accountId, Pageable pageable);
}
