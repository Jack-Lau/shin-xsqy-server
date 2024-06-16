/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.baccarat;

import java.util.List;
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
public interface BaccaratBetRepository extends JpaRepository<BaccaratBet, Long> {

    @Override
    <S extends BaccaratBet> S save(S entity);

    @Query("SELECT b FROM BaccaratBet b WHERE b.accountId = ?1 AND b.gameId = ?2")
    BaccaratBet findByAccountIdAndGameId(long accountId, long gameId);

    @Query("SELECT b FROM BaccaratBet b WHERE b.accountId = ?1 AND b.gameId = ?2")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BaccaratBet> findByAccountIdAndGameIdForWrite(long accountId, long gameId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<BaccaratBet> findByAccountId(long accountId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<BaccaratBet> findByGameId(long gameId);

}
