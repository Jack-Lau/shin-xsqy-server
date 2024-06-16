/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.redPacket;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.util.List;
import lombok.Value;

/**
 *
 * @author Administrator
 */
@Value
@WebMessageType
public class RedPacketOverall {

    private List<RedPacket> redPackets;
    private RedPacketSelfRecord redPacketSelfRecord;

    public RedPacketOverall(List<RedPacket> redPackets, RedPacketSelfRecord redPacketSelfRecord) {
        this.redPackets = redPackets;
        this.redPacketSelfRecord = redPacketSelfRecord;
    }

}
