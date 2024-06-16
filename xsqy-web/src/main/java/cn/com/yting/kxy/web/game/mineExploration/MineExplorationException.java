/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.mineExploration;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Darkholme
 */
public class MineExplorationException extends KxyWebException {

    public static final int EC_玩法未开启 = 3000;
    public static final int EC_角色等级不足 = 3001;
    public static final int EC_正在游戏中 = 3002;
    public static final int EC_不在游戏 = 3003;
    public static final int EC_可挖掘次数不足 = 3004;
    public static final int EC_该格已被挖掘 = 3005;
    public static final int EC_剩余可挖掘次数 = 3006;
    public static final int EC_没有可挖掘地块 = 3007;
    public static final int EC_代金券不存在 = 3008;
    public static final int EC_代金券已领取 = 3009;
    public static final int EC_仙石不足 = 3010;

    public MineExplorationException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static MineExplorationException 玩法未开启() {
        return new MineExplorationException(EC_玩法未开启, "玩法未开启");
    }

    public static MineExplorationException 角色等级不足() {
        return new MineExplorationException(EC_角色等级不足, "角色等级不足");
    }

    public static MineExplorationException 正在游戏中() {
        return new MineExplorationException(EC_正在游戏中, "正在游戏中");
    }

    public static MineExplorationException 不在游戏() {
        return new MineExplorationException(EC_不在游戏, "不在游戏");
    }

    public static MineExplorationException 可挖掘次数不足() {
        return new MineExplorationException(EC_可挖掘次数不足, "可挖掘次数不足");
    }

    public static MineExplorationException 该格已被挖掘() {
        return new MineExplorationException(EC_该格已被挖掘, "该格已被挖掘");
    }

    public static MineExplorationException 剩余可挖掘次数() {
        return new MineExplorationException(EC_剩余可挖掘次数, "剩余可挖掘次数");
    }

    public static MineExplorationException 没有可挖掘地块() {
        return new MineExplorationException(EC_没有可挖掘地块, "没有可挖掘地块");
    }

    public static MineExplorationException 代金券不存在() {
        return new MineExplorationException(EC_代金券不存在, "代金券不存在");
    }

    public static MineExplorationException 代金券已领取() {
        return new MineExplorationException(EC_代金券已领取, "代金券已领取");
    }

    public static MineExplorationException 仙石不足() {
        return new MineExplorationException(EC_仙石不足, "仙石不足");
    }

}
