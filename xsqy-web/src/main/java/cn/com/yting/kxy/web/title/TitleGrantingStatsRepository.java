/*
 * Created 2018-11-8 11:00:13
 */
package cn.com.yting.kxy.web.title;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface TitleGrantingStatsRepository extends JpaRepository<TitleGrantingStats, Long> {

    @Query("SELECT t FROM TitleGrantingStats t WHERE t.titleId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TitleGrantingStats> findByIdForWrite(long titleId);

    default TitleGrantingStats findOrCreateById(long titleId) {
        return findByIdForWrite(titleId).orElseGet(() -> {
            TitleGrantingStats stats = new TitleGrantingStats();
            stats.setTitleId(titleId);
            return save(stats);
        });
    }
}
