/*
 * Created 2018-9-26 12:00:08
 */
package cn.com.yting.kxy.web.party;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
public interface PartyRepository extends JpaRepository<PartyRecord, Long> {

    @Query("SELECT r FROM PartyRecord r WHERE r.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    PartyRecord findByIdForWrite(long accountId);

    @Transactional
    default PartyRecord findOrCreateById(long accountId) {
        PartyRecord record = findByIdForWrite(accountId);
        if (record == null) {
            record = new PartyRecord();
            record.setAccountId(accountId);
            record = saveAndFlush(record);
        }
        return record;
    }
}
