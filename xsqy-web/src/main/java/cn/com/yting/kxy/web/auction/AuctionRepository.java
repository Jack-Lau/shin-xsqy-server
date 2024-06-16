/*
 * Created 2018-11-12 18:50:46
 */
package cn.com.yting.kxy.web.auction;

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
public interface AuctionRepository extends JpaRepository<AuctionRecord, Long> {

    @Query("SELECT r FROM AuctionRecord r WHERE r.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AuctionRecord> findByIdForWrite(long id);

    @Modifying
    @Query("UPDATE AuctionRecord r SET r.likedToday = 0")
    void resetLikedToday();

    @Query("SELECT r FROM AuctionRecord r")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<AuctionRecord> findAllForWrite();
}
