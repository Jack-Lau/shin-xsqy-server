/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.baccarat;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.util.List;
import lombok.Value;

/**
 *
 * @author Darkholme
 */
@Value
@WebMessageType
public class BaccaratOverall {

    private boolean AVAILABLE;
    private BaccaratConstants.Status status;
    private long currentGameId;
    private List<Long> currentBet;

    private BaccaratBet baccaratBet;
    private BaccaratGame baccaratGame;

}
