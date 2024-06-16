/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.treasureBowl;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Administrator
 */
public interface TreasureBowlRepository extends JpaRepository<TreasureBowlRecord, Long> {

    @Override
    <S extends TreasureBowlRecord> S save(S entity);

}
