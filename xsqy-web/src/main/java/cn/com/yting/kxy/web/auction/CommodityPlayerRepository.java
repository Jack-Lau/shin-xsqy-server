/*
 * Created 2018-11-15 18:30:33
 */
package cn.com.yting.kxy.web.auction;

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
public interface CommodityPlayerRepository extends JpaRepository<CommodityPlayerRecord, CommodityPlayerRecord.PK> {

    @Query("SELECT r FROM CommodityPlayerRecord r WHERE r.commodityId = ?1 AND r.accountId = ?2")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CommodityPlayerRecord> findByIdForWrite(long commodityId, long accountId);

    List<CommodityPlayerRecord> findByCommodityId(long commodityId);

    List<CommodityPlayerRecord> findByAccountId(long accountId);

    default CommodityPlayerRecord findOrCreateById(long commodityId, long accountId) {
        return findByIdForWrite(commodityId, accountId).orElseGet(() -> {
            CommodityPlayerRecord record = new CommodityPlayerRecord();
            record.setCommodityId(commodityId);
            record.setAccountId(accountId);
            return save(record);
        });
    }

    @Query("SELECT COALESCE(SUM(r.likeCount), 0) FROM CommodityPlayerRecord r WHERE r.commodityId = ?1")
    int sumLikeCountByCommodityId(long commodityId);

    @Query("SELECT COUNT(r) FROM CommodityPlayerRecord r WHERE r.commodityId = ?1 AND r.bidded = true")
    int countBidderByCommodityId(long commodityId);
}
