/*
 * Created 2019-1-21 18:25:18
 */
package cn.com.yting.kxy.web.game.fuxingjianglin;

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
public interface FxjlRepository extends JpaRepository<FxjlRecord, Long> {

    @Query("FROM FxjlRecord r WHERE r.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<FxjlRecord> findByIdForWrite(long accountId);

    @Query("FROM FxjlRecord")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<FxjlRecord> findAllForWrite();
}
