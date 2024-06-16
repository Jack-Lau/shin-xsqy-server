/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.slots;

import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

/**
 *
 * @author Darkholme
 */
public interface SlotsRepository extends JpaRepository<SlotsRecord, Long> {

    @Override
    <S extends SlotsRecord> S save(S entity);

    @Query("SELECT e FROM SlotsRecord e WHERE e.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SlotsRecord> findByAccountIdForWrite(long accountId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    SlotsRecord findByAccountId(long accountId);

}
