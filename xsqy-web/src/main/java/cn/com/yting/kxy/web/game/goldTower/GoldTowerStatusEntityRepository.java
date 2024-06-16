/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower;

import java.util.List;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Darkholme
 */
public interface GoldTowerStatusEntityRepository extends JpaRepository<GoldTowerStatusEntity, Long> {

    @Override
    <S extends GoldTowerStatusEntity> S save(S entity);

    @Query("FROM GoldTowerStatusEntity")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<GoldTowerStatusEntity> findAllForWrite();

}
