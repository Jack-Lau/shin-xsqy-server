/*
 * Created 2018-11-20 15:24:16
 */
package cn.com.yting.kxy.web.impartation;

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
public interface ImpartationRepository extends JpaRepository<ImpartationRecord, Long> {

    @Query("SELECT r FROM ImpartationRecord r WHERE r.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ImpartationRecord> findByIdForWrite(long accountId);

    @Query("SELECT NEW cn.com.yting.kxy.web.impartation.CandidateMaster(r.accountId, COUNT(d))"
        + " FROM ImpartationRecord r"
        + " LEFT JOIN DiscipleRecord d"
        + " ON r.accountId = d.masterAccountId"
        + " AND d.phase != cn.com.yting.kxy.web.impartation.DisciplinePhase.END"
        + " WHERE r.role = cn.com.yting.kxy.web.impartation.ImpartationRole.MASTER"
        + " GROUP BY r.accountId"
        + " HAVING COUNT(d) < " + ImpartationConstants.DISCIPLES_COUNT_LIMIT)
    List<CandidateMaster> findAcceptableMasterAccountIds();
}
