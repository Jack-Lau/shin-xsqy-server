/*
 * Created 2019-1-7 16:51:24
 */
package cn.com.yting.kxy.web.perk;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface PerkRingRepository extends JpaRepository<PerkRing, Long> {

    @Query("FROM PerkRing WHERE accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    PerkRing findByIdForWrite(long accountId);
}
