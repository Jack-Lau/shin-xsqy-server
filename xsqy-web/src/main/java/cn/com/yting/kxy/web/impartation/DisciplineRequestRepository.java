/*
 * Created 2018-11-20 18:22:23
 */
package cn.com.yting.kxy.web.impartation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
public interface DisciplineRequestRepository extends JpaRepository<DisciplineRequest, DisciplineRequest.PK> {

    List<DisciplineRequest> findByAccountId(long accountId);

    List<DisciplineRequest> findByMasterAccountId(long masterAccountId);

    default Optional<DisciplineRequest> findById(long accountId, long masterAccountId) {
        return findById(new DisciplineRequest.PK(accountId, masterAccountId));
    }

    @Modifying
    @Query("DELETE FROM DisciplineRequest r WHERE r.accountId = ?1")
    void deleteInBulkByAccountId(long accountId);

    @Modifying
    @Query("DELETE FROM DisciplineRequest r WHERE r.masterAccountId = ?1")
    @Transactional
    void deleteInBulkByMasterAccountId(long masterAccountId);
}
