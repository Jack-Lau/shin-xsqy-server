/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.mineExploration;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Getter;

/**
 *
 * @author Darkholme
 */
@WebMessageType
@Getter
public class MineExplorationGrid {

    public int type = -1;
    public long currencyId;
    public long amount;
    public boolean isOpen = true;

    @Override
    public String toString() {
        return type + "-" + currencyId + "-" + amount;
    }

}
