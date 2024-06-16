/*
 * Created 2018-12-4 10:52:09
 */
package cn.com.yting.kxy.web.ethereum;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface ExchangeRepository extends JpaRepository<ExchangeRecord, Long> {

    @Query("SELECT r FROM ExchangeRecord r WHERE r.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ExchangeRecord> findByIdForWrite(long accountId);

    default ExchangeRecord findOrCreateRecord(long accountId) {
        return findByIdForWrite(accountId).orElseGet(() -> {
            ExchangeRecord record = new ExchangeRecord();
            record.setAccountId(accountId);
            return save(record);
        });
    }
}
