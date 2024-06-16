/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.mineExploration;

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
public interface MineExplorationRecordRepository extends JpaRepository<MineExplorationRecord, Long> {

    @Override
    <S extends MineExplorationRecord> S save(S entity);

    @Query("SELECT m FROM MineExplorationRecord m WHERE m.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<MineExplorationRecord> findByAccountIdForWrite(long accountId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    MineExplorationRecord findByAccountId(long accountId);

    @Query("SELECT m FROM MineExplorationRecord m WHERE m.accountId IN ?1 AND m.couponTake < 10")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<MineExplorationRecord> findCanSendCoupon(List<Long> accountIds);

}
