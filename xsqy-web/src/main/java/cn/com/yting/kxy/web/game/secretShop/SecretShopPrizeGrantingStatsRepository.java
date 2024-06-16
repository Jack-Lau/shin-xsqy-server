/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.secretShop;

import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Darkholme
 */
public interface SecretShopPrizeGrantingStatsRepository extends JpaRepository<SecretShopPrizeGrantingStats, Long> {

    @Query("SELECT s FROM SecretShopPrizeGrantingStats s WHERE s.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SecretShopPrizeGrantingStats> findByIdForWrite(long id);

    default SecretShopPrizeGrantingStats findOrCreateById(long id) {
        return findByIdForWrite(id).orElseGet(() -> {
            SecretShopPrizeGrantingStats stats = new SecretShopPrizeGrantingStats();
            stats.setId(id);
            return save(stats);
        });
    }

}
