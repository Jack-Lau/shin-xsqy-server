/*
 * Created 2018-10-30 16:20:56
 */
package cn.com.yting.kxy.web.ranking;

import java.util.Date;
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
public interface RankingRepository extends JpaRepository<RankingRecord, RankingRecord.PK> {

    List<RankingRecord> findByRankingIdAndAccountId(long rankingId, long accountId);

    @Query("SELECT r FROM RankingRecord r WHERE r.rankingId = ?1 AND r.accountId = ?2 AND r.objectId = ?3")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RankingRecord> findByIdForWrite(long rankingId, long accountId, long objectId);

    default RankingRecord findOrCreateById(long rankingId, long accountId, long objectId) {
        return findByIdForWrite(rankingId, accountId, objectId).orElseGet(() -> {
            RankingRecord record = new RankingRecord();
            record.setRankingId(rankingId);
            record.setAccountId(accountId);
            record.setObjectId(objectId);
            record.setLastModified(new Date());
            return save(record);
        });
    }

    @Query("SELECT r FROM RankingRecord r WHERE r.rankingId = ?1 AND r.rankingValue_1 != 0"
        + " ORDER BY rankingValue_1, ranking_value_2, ranking_value_3, ranking_value_4, ranking_value_5, lastModified, accountId")
    List<RankingRecord> findByRankingId(long rankingId);

    @Modifying
    @Query("UPDATE RankingRecord r SET"
        + " r.rankingValue_1 = 0,"
        + " r.rankingValue_2 = 0,"
        + " r.rankingValue_3 = 0,"
        + " r.rankingValue_4 = 0,"
        + " r.rankingValue_5 = 0"
        + " WHERE r.rankingId = ?1"
    )
    void resetRankingValueByRankingId(long rankingId);
}
