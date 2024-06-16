/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower;

import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.message.WebMessageType;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Data
@WebMessageType
public class GoldTowerWipeOut {

    private GoldTowerChallengeEntity goldTowerChallengeEntity;
    private List<CurrencyStack> wipeOutAwards;

    public GoldTowerWipeOut(GoldTowerChallengeEntity goldTowerChallengeEntity, List<CurrencyStack> wipeOutAwards) {
        this.goldTowerChallengeEntity = goldTowerChallengeEntity;
        this.wipeOutAwards = wipeOutAwards;
    }

}
