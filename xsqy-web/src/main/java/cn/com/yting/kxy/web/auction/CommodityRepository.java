/*
 * Created 2018-11-14 11:10:45
 */
package cn.com.yting.kxy.web.auction;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
public interface CommodityRepository extends JpaRepository<Commodity, Long> {

    @Query("SELECT c FROM Commodity c WHERE c.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Commodity> findByIdForWrite(long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Commodity> findByCommodityStatus(CommodityStatus status);

    boolean existsByQueueNumber(int queueNumber);

    @Query("SELECT c FROM Commodity c"
        + " WHERE c.lastBidderAccountId = ?1"
        + " AND c.commodityStatus = cn.com.yting.kxy.web.auction.CommodityStatus.SOLD"
        + " AND c.delivered = false")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    List<Commodity> findDeliverable(long accountId);
}
