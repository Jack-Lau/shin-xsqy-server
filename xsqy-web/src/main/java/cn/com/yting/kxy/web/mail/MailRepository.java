/*
 * Created 2018-7-23 11:56:17
 */
package cn.com.yting.kxy.web.mail;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface MailRepository extends JpaRepository<Mail, Long> {

    @Query("SELECT m FROM Mail m WHERE m.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Mail> findByIdForWrite(long id);

    List<Mail> findByAccountIdOrderByIdDesc(long accountId, Pageable pageable);

    @Query("SELECT COUNT(m) > 0 FROM Mail m WHERE m.accountId = ?1 AND m.alreadyRead IS FALSE")
    boolean existsByAccountIdAndUnread(long accountId);

    @Modifying
    @Query("DELETE FROM Mail m WHERE m.accountId = ?1 AND m.alreadyRead IS TRUE AND m.attachmentDelivered IS TRUE")
    void deleteNeedlessByAccountId(long accountId);

    @Modifying
    @Query("DELETE FROM Mail m WHERE m.createTime < ?1")
    void deleteExpired(Date expireTime);
}
