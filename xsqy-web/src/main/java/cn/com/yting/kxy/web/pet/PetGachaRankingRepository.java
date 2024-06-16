/*
 * Created 2018-10-12 15:33:11
 */
package cn.com.yting.kxy.web.pet;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface PetGachaRankingRepository extends JpaRepository<PetGachaRankingRecord, Long> {

    @Query("SELECT r FROM PetGachaRankingRecord r WHERE r.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    PetGachaRankingRecord findByIdForWrite(long accountId);

    @Query("SELECT r FROM PetGachaRankingRecord r ORDER BY r.point DESC, r.lastModified ASC")
    List<PetGachaRankingRecord> findOrderByRanking(Pageable pageable);

    default List<PetGachaRankingRecord> findFirst100Ordered() {
        return findOrderByRanking(PageRequest.of(0, 100));
    }
}
