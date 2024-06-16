/*
 * Created 2018-7-6 16:46:34
 */
package cn.com.yting.kxy.web.game.kuaibidazhuanpan;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface KbdzpRepository extends JpaRepository<KbdzpRecord, Long> {

    @Query("SELECT k FROM KbdzpRecord k WHERE k.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    KbdzpRecord findByIdForWrite(long accountId);
}
