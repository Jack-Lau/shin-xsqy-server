/*
 * Created 2018-11-5 19:06:27
 */
package cn.com.yting.kxy.web.game.antique;

import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

/**
 *
 * @author Azige
 */
public interface AntiqueRepository extends JpaRepository<AntiqueRecord, Long> {

    @Override
    <S extends AntiqueRecord> S save(S entity);

    @Query("SELECT e FROM AntiqueRecord e WHERE e.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AntiqueRecord> findByAccountIdForWrite(long accountId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    AntiqueRecord findByAccountId(long accountId);

}
