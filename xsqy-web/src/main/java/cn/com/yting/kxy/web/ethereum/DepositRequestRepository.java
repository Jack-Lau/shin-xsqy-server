/*
 * Created 2018-8-31 16:04:23
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
public interface DepositRequestRepository extends JpaRepository<DepositRequest, Long> {

    @Query("SELECT d FROM DepositRequest d WHERE d.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    DepositRequest findByIdForWrite(long id);

    List<DepositRequest> findByRequestStatus(DepositRequestStatus requestStatus);

    List<DepositRequest> findByAccountIdAndRequestStatus(long accountId, DepositRequestStatus requestStatus);

    long countByAccountId(long accountId);
}
