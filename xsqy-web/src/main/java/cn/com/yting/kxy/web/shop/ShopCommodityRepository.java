/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.shop;

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
public interface ShopCommodityRepository extends JpaRepository<ShopCommodityRecord, Long> {

    @Override
    <S extends ShopCommodityRecord> S save(S entity);

    @Query("SELECT e FROM ShopCommodityRecord e WHERE e.commodityId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ShopCommodityRecord> findByCommodityIdForWrite(long commodityId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    ShopCommodityRecord findByCommodityId(long commodityId);

}
