/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.player;

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
public interface PlayerLocationRepository extends JpaRepository<PlayerLocation, Long> {

    @Override
    <S extends PlayerLocation> S save(S entity);

    @Query("SELECT e FROM PlayerLocation e WHERE e.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PlayerLocation> findByAccountIdForWrite(long accountId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    PlayerLocation findByAccountId(long accountId);

}
