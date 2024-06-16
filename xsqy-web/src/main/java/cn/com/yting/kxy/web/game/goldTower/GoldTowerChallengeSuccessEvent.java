/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Darkholme
 */
public class GoldTowerChallengeSuccessEvent extends KxyWebEvent {

    private final GoldTowerChallengeEntity goldTowerChallengeEntity;
    private final int successCount;

    public GoldTowerChallengeSuccessEvent(Object source, GoldTowerChallengeEntity goldTowerChallengeEntity, int successCount) {
        super(source);
        this.goldTowerChallengeEntity = goldTowerChallengeEntity;
        this.successCount = successCount;
    }

    /**
     * @return the goldTowerChallengeEntity
     */
    public GoldTowerChallengeEntity getGoldTowerChallengeEntity() {
        return goldTowerChallengeEntity;
    }

    /**
     * @return the successCount
     */
    public int getSuccessCount() {
        return successCount;
    }

}
