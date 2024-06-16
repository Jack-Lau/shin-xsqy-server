/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.treasureBowl;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Administrator
 */
@Value
@WebMessageType
public class TreasureBowlOverall {

    private TreasureBowl treasureBowl;
    private TreasureBowlSelfRecord treasureBowlSelfRecord;

    public TreasureBowlOverall(TreasureBowl treasureBowl, TreasureBowlSelfRecord treasureBowlSelfRecord) {
        this.treasureBowl = treasureBowl;
        this.treasureBowlSelfRecord = treasureBowlSelfRecord;
    }

}
