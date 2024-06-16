/*
 * Created 2018-6-26 16:18:35
 */
package cn.com.yting.kxy.web.player;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface PlayerRepository extends JpaRepository<Player, Long> {

    boolean existsByPlayerName(String playerName);

    Player findByPlayerName(String playerName);

    @Query("SELECT p.accountId FROM Player p")
    List<Long> findAccountId();

    @Query("SELECT p.accountId FROM Player p WHERE p.accountId != ?1")
    List<Long> findAccountIdExcludeSelf(long id);

    @Query("SELECT p FROM Player p WHERE p.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Player findByIdForWrite(long accountId);

    @Query("SELECT p.accountId"
            + " FROM Player p"
            + " WHERE p.fc BETWEEN ?2 and ?3"
            + " AND p.accountId != ?1"
            + " AND NOT EXISTS (SELECT 1 FROM SupportRelation WHERE inviterAccountId = ?1 AND supporterAccountId = p.accountId)"
            + " AND (SELECT COUNT(*) FROM SupportRelation WHERE supporterAccountId = p.accountId) < ?4")
    List<Long> findAccountIdByCandidateSupporterCondition(
            long accountId,
            long lowerBound,
            long upperBound,
            long supportCountLimit
    );

    @Query(value = "SELECT *"
            + " FROM player p"
            + " WHERE p.fc BETWEEN ?2 and ?3"
            + " AND p.account_id != ?1"
            + " ORDER BY RAND()"
            + " LIMIT ?4", nativeQuery = true)
    List<Player> findPlayerBetweenFcExcludeSelf(
            long accountId,
            long lowerBound,
            long upperBound,
            int count
    );

    @Query(value = "SELECT *"
            + " FROM player p"
            + " WHERE p.player_level BETWEEN ?2 and ?3"
            + " AND p.account_id != ?1"
            + " ORDER BY RAND()"
            + " LIMIT ?4", nativeQuery = true)
    List<Player> findPlayerBetweenLvExcludeSelf(
            long accountId,
            long lowerBound,
            long upperBound,
            int count
    );

    @Query(value = "SELECT COUNT(*)"
            + " FROM Player p"
            + " WHERE p.fc BETWEEN ?1 and ?2"
            + " AND p.accountId != ?3"
            + " AND p.accountId != ?4"
            + " AND p.accountId != ?5")
    int countPlayersExcludeMyTeamByFC(long startFC, long endFC, long accountId, long accountId_su1, long accountId_su2);

    @Query(value = "SELECT * FROM player p"
            + " WHERE p.fc BETWEEN ?1 and ?2"
            + " AND p.account_id!=?3"
            + " AND p.account_id!=?4"
            + " AND p.account_id!=?5"
            + " ORDER BY RAND()"
            + " LIMIT ?6", nativeQuery = true)
    List<Player> findPlayersExcludeMyTeamByFC(long startFC, long endFC, long accountId, long accountId_su1, long accountId_su2, int number);

    @Query("SELECT p FROM Player p JOIN SchoolRecord s ON p.accountId = s.accountId WHERE s.schoolId = ?1")
    List<Player> findBySchoolId(long schoolId);

    @Query("SELECT NEW cn.com.yting.kxy.web.player.PlayerLevelAndExp(p.accountId, p.playerLevel, c.amount)"
            + " FROM Player p JOIN CurrencyRecord c ON p.accountId = c.accountId"
            + " WHERE c.currencyId = 153")
    List<PlayerLevelAndExp> findPlayerLevelAndExp();
}
