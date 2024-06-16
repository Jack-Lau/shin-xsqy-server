/*
 * Created 2018-7-9 16:15:29
 */
package cn.com.yting.kxy.web.invitation;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface InviterRepository extends JpaRepository<InviterRecord, Long> {

    boolean existsByInvitationCode(String code);

    InviterRecord findByInvitationCode(String code);

    @Query("SELECT r FROM InviterRecord r WHERE r.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InviterRecord> findByIdForWrite(long accountId);
}
