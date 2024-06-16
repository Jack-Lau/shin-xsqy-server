/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.redPacket;

import java.util.List;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

/**
 *
 * @author Administrator
 */
public interface RedPacketOpenRepository extends JpaRepository<RedPacketOpenRecord, Long> {

    @Override
    <S extends RedPacketOpenRecord> S save(S entity);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<RedPacketOpenRecord> findByAccountId(long accountId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<RedPacketOpenRecord> findByRedPacketId(long redPacketId);

}
