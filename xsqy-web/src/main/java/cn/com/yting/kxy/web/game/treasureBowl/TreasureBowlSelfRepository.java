/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.treasureBowl;

import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

/**
 *
 * @author Administrator
 */
public interface TreasureBowlSelfRepository extends JpaRepository<TreasureBowlSelfRecord, Long> {

    @Override
    <S extends TreasureBowlSelfRecord> S save(S entity);

    @Query("SELECT e FROM TreasureBowlSelfRecord e WHERE e.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TreasureBowlSelfRecord> findByAccountIdForWrite(long accountId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    TreasureBowlSelfRecord findByAccountId(long accountId);

}
