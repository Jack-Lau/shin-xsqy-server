/*
 * Created 2018-11-20 16:14:59
 */
package cn.com.yting.kxy.web.impartation;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;

/**
 *
 * @author Azige
 */
public interface DiscipleRepository extends JpaRepository<DiscipleRecord, Long> {

    List<DiscipleRecord> findByMasterAccountId(long masterAccountId);

    @Query("SELECT r FROM DiscipleRecord r WHERE r.masterAccountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<DiscipleRecord> findByMasterAccountIdForWrite(long masterAccountId);

    @Query("SELECT r FROM DiscipleRecord r WHERE r.accountId = ?1")
    Optional<DiscipleRecord> findByIdForWrite(long accountId);

    @Query("SELECT COUNT(r) FROM DiscipleRecord r"
        + " WHERE r.masterAccountId = ?1"
        + " AND r.phase != cn.com.yting.kxy.web.impartation.DisciplinePhase.END")
    int countByNotEndDisciples(long masterAccountId);

    @Modifying
    @Query(value = "UPDATE disciple_record r"
        + " JOIN player p ON p.account_id = r.account_id"
        + " SET r.daily_practice_generated = false, r.player_level_at_midnight = p.player_level,"
        + " r.yesterday_contribution_pool = r.today_contribution_pool, r.today_contribution_pool = 0,"
        + " r.yesterday_exp_pool = r.today_exp_pool, r.today_exp_pool = 0"
        + " WHERE r.discipline_phase = 'PRACTISING'",
        nativeQuery = true)
    void dailyUpdate();

    @Modifying
    @Query("UPDATE DiscipleRecord r"
        + " SET r.phase = cn.com.yting.kxy.web.impartation.DisciplinePhase.TO_BE_CONFIRMED"
        + " WHERE r.deadline <= ?1 AND r.phase = cn.com.yting.kxy.web.impartation.DisciplinePhase.PRACTISING")
    void updateEndPhaseByDeadlineReached(@Temporal(TemporalType.DATE) Date today);
}
