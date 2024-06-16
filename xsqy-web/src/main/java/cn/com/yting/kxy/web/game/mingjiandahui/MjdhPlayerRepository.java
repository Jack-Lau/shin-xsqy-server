/*
 * Created 2018-12-12 13:08:59
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface MjdhPlayerRepository extends JpaRepository<MjdhPlayerRecord, MjdhPlayerRecord.PK> {

    default Optional<MjdhPlayerRecord> findById(long seasonId, long accountId) {
        return findById(new MjdhPlayerRecord.PK(seasonId, accountId));
    }

    @Query("SELECT r FROM MjdhPlayerRecord r WHERE r.seasonId = ?1 AND r.accountId = ?2")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<MjdhPlayerRecord> findByIdForWrite(long seasonId, long accountId);

    List<MjdhPlayerRecord> findBySeasonId(long seasonId);

    List<MjdhPlayerRecord> findByGrade(int grade);

    int countBySeasonId(long seasonId);
}
