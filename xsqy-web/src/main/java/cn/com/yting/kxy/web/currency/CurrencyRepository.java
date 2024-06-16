/*
 * Created 2018-6-26 17:56:46
 */
package cn.com.yting.kxy.web.currency;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import cn.com.yting.kxy.web.currency.CurrencyRecord.CurrencyRecordPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface CurrencyRepository extends JpaRepository<CurrencyRecord, CurrencyRecordPK> {

    List<CurrencyRecord> findByAccountId(long accountId);

    @Query("FROM CurrencyRecord r WHERE r.accountId = ?1 AND r.currencyId = ?2")
    Optional<CurrencyRecord> findById(long accountId, long currencyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("FROM CurrencyRecord r WHERE r.accountId = ?1 AND r.currencyId = ?2")
    Optional<CurrencyRecord> findByIdForWrite(long accountId, long currencyId);

    @Modifying
    @Query
    void deleteByCurrencyId(long currencyId);
}
