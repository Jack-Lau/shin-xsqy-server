/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.slots;

import cn.com.yting.kxy.web.award.AwardResult;
import cn.com.yting.kxy.web.message.WebMessageType;
import java.util.List;
import lombok.Value;

/**
 *
 * @author Darkholme
 */
@Value
@WebMessageType
public class SlotsOverall {

    private SlotsRecord slotsRecord;
    private Long prizeId;
    private List<AwardResult> awardResults;

}
