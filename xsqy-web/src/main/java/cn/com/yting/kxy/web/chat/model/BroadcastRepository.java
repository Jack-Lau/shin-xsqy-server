/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.chat.model;

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
public interface BroadcastRepository extends JpaRepository<BroadcastRecord, Long> {

    @Override
    <S extends BroadcastRecord> S save(S entity);

    @Query("SELECT e FROM BroadcastRecord e WHERE e.broadcastId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BroadcastRecord> findByBroadcastIdForWrite(long broadcastId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    BroadcastRecord findByBroadcastId(long broadcastId);

}
