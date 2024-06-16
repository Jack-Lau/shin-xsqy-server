/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.redPacket;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Administrator
 */
public interface RedPacketRepository extends JpaRepository<RedPacketRecord, Long> {

    @Query("SELECT e FROM RedPacketRecord e WHERE e.finish = false")
    List<RedPacketRecord> findNotFinish();

}
