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
public class RedPacket {

    private RedPacketRecord redPacketRecord;
    private List<RedPacketOpenRecord> redPacketOpenRecords;

    public RedPacket(RedPacketRecord redPacketRecord, List<RedPacketOpenRecord> redPacketOpenRecords) {
        this.redPacketRecord = redPacketRecord;
        this.redPacketOpenRecords = redPacketOpenRecords;
    }

}
