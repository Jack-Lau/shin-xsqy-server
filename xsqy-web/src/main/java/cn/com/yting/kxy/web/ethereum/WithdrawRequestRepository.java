/*
 * Created 2018-8-28 15:32:47
 */
package cn.com.yting.kxy.web.ethereum;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface WithdrawRequestRepository extends JpaRepository<WithdrawRequest, Long> {

    @Query("SELECT w FROM WithdrawRequest w WHERE w.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    WithdrawRequest findByIdForWrite(long id);

    List<WithdrawRequest> findByRequestStatus(WithdrawRequestStatus requestStatus);

    List<WithdrawRequest> findByAccountIdAndRequestStatus(long accountId, WithdrawRequestStatus requestStatus);

    long countByAccountId(long accountId);
}
