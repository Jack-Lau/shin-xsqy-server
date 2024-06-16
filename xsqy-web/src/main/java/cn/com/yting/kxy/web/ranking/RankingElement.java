/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.ranking;

import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.player.PlayerBaseInfo;
import lombok.Value;

/**
 *
 * @author Darkholme
 */
@Value
@WebMessageType
public class RankingElement {

    private PlayerBaseInfo playerBaseInfo;
    private int currentRank;
    private long rankValue;

}
