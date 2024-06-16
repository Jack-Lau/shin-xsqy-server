/*
 * Created 2018-9-12 12:25:05
 */
package cn.com.yting.kxy.web.school;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface SchoolRepository extends JpaRepository<SchoolRecord, Long> {

    @Query("SELECT r FROM SchoolRecord r WHERE r.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SchoolRecord> findByIdForWrite(long accountId);
}
