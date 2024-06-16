/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.mineExploration;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.util.List;
import lombok.Value;

/**
 *
 * @author Darkholme
 */
@Value
@WebMessageType
public class MineExplorationOverall {

    private boolean inGame;
    private int availableDig;
    private MineExplorationGrid[][] map;
    private MineExplorationGrid bigAwardA;
    private MineExplorationGrid bigAwardB;
    private List<MineExplorationCouponSend> coupons;

}
