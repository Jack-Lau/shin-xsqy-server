/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.treasureBowl;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.util.List;
import lombok.Value;

/**
 *
 * @author Administrator
 */
@Value
@WebMessageType
public class TreasureBowl {

    private TreasureBowlRecord treasureBowlRecord;
    private List<TreasureBowlAttendRecord> treasureBowlAttendRecords;

    public TreasureBowl(TreasureBowlRecord treasureBowlRecord, List<TreasureBowlAttendRecord> treasureBowlAttendRecords) {
        this.treasureBowlRecord = treasureBowlRecord;
        this.treasureBowlAttendRecords = treasureBowlAttendRecords;
    }

}
