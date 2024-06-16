/*
 * Created 2019-2-13 16:30:02
 */
package cn.com.yting.kxy.web.game.yuanxiaojiayao;

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
public interface YxjyRepository extends JpaRepository<YxjyRecord, Long> {

    @Query("FROM YxjyRecord r WHERE r.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<YxjyRecord> findByIdForWrite(long accountId);
}
