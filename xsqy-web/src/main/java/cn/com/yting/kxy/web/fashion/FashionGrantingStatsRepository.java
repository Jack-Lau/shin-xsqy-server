/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.fashion;

import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Darkholme
 */
public interface FashionGrantingStatsRepository extends JpaRepository<FashionGrantingStats, Long> {

    @Query("SELECT f FROM FashionGrantingStats f WHERE f.definitionId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<FashionGrantingStats> findByIdForWrite(long definitionId);

    default FashionGrantingStats findOrCreateById(long definitionId) {
        return findByIdForWrite(definitionId).orElseGet(() -> {
            FashionGrantingStats stats = new FashionGrantingStats();
            stats.setDefinitionId(definitionId);
            return save(stats);
        });
    }

}
