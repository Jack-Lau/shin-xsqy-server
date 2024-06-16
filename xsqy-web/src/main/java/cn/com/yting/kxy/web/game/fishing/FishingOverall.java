/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.fishing;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.util.List;
import lombok.Value;

/**
 *
 * @author Administrator
 */
@Value
@WebMessageType
public class FishingOverall {

    private FishingRecord fishingRecord;
    private List<FishingOnceRecord> fishingOnceRecords;

    public FishingOverall(FishingRecord fishingRecord, List<FishingOnceRecord> fishingOnceRecords) {
        this.fishingRecord = fishingRecord;
        this.fishingOnceRecords = fishingOnceRecords;
    }

}
