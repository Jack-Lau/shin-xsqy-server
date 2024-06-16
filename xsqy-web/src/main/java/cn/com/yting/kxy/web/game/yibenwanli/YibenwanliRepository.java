/*
 * Created 2018-9-4 15:32:08
 */
package cn.com.yting.kxy.web.game.yibenwanli;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface YibenwanliRepository extends JpaRepository<YibenwanliRecord, Long> {

    @Query("SELECT r FROM YibenwanliRecord r WHERE r.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    YibenwanliRecord findByIdForWrite(long accountId);
}
