/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.brawl;

import cn.com.yting.kxy.web.battle.BattleResponse;
import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.player.PlayerBaseInfo;
import java.util.List;
import lombok.Value;

/**
 *
 * @author Darkholme
 */
@Value
@WebMessageType
public class BrawlOverall {

    public BrawlOverall(BrawlRecord brawlRecord, List<PlayerBaseInfo> teamMembers) {
        this.brawlRecord = brawlRecord;
        this.teamMembers = teamMembers;
        this.enemies = null;
        this.battleResponse = null;
        this.isBattleWin = false;
        this.awardAmount = 0;
    }

    public BrawlOverall(BrawlRecord brawlRecord, List<PlayerBaseInfo> teamMembers, List<PlayerBaseInfo> enemies, BattleResponse battleResponse) {
        this.brawlRecord = brawlRecord;
        this.teamMembers = teamMembers;
        this.enemies = enemies;
        this.battleResponse = battleResponse;
        this.isBattleWin = false;
        this.awardAmount = 0;
    }

    public BrawlOverall(BrawlRecord brawlRecord, List<PlayerBaseInfo> teamMembers, boolean isBattleWin) {
        this.brawlRecord = brawlRecord;
        this.teamMembers = teamMembers;
        this.enemies = null;
        this.battleResponse = null;
        this.isBattleWin = isBattleWin;
        this.awardAmount = 0;
    }

    public BrawlOverall(BrawlRecord brawlRecord, List<PlayerBaseInfo> teamMembers, long awardAmount) {
        this.brawlRecord = brawlRecord;
        this.teamMembers = teamMembers;
        this.enemies = null;
        this.battleResponse = null;
        this.isBattleWin = false;
        this.awardAmount = awardAmount;
    }

    private BrawlRecord brawlRecord;
    private List<PlayerBaseInfo> teamMembers;
    private List<PlayerBaseInfo> enemies;
    private BattleResponse battleResponse;
    private boolean isBattleWin;
    private long awardAmount;

}
