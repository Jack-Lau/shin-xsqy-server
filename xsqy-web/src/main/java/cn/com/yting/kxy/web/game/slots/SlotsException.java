/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.slots;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Darkholme
 */
public class SlotsException extends KxyWebException {

    public static final int EC_NOT_STARTED_YET = 2500;
    public static final int EC_INSUFFICIENT_PLAYER_LEVEL = 2501;
    public static final int EC_INSUFFICIENT_CURRENCY = 2502;
    public static final int EC_LOCK_MAX = 2503;
    public static final int EC_TAKEN_PRIZE = 2504;
    public static final int EC_LIKE_SEND_MAX = 2505;
    public static final int EC_LIKED = 2506;
    public static final int EC_BIG_PRIZE_NOT_EXIST = 2507;
    public static final int EC_NOT_FRIEND = 2508;
    public static final int EC_EMPTY_LIKE = 2509;

    public SlotsException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static SlotsException notStartedYet() {
        return new SlotsException(EC_NOT_STARTED_YET, "活动未开启");
    }

    public static SlotsException insufficientPlayerLevel() {
        return new SlotsException(EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足");
    }

    public static SlotsException insufficientCurrency() {
        return new SlotsException(EC_INSUFFICIENT_CURRENCY, "拥有的货币不足");
    }

    public static SlotsException lockMax() {
        return new SlotsException(EC_LOCK_MAX, "锁的数量已达上限");
    }

    public static SlotsException takenPrize() {
        return new SlotsException(EC_TAKEN_PRIZE, "当前没有奖励可领");
    }

    public static SlotsException likeSendMax() {
        return new SlotsException(EC_LIKE_SEND_MAX, "已达到当日点赞上限");
    }

    public static SlotsException liked() {
        return new SlotsException(EC_LIKED, "已经给这条记录点赞了");
    }

    public static SlotsException bigPrizeNotExist() {
        return new SlotsException(EC_BIG_PRIZE_NOT_EXIST, "大奖记录不存在");
    }

    public static SlotsException notFriend() {
        return new SlotsException(EC_NOT_FRIEND, "不是好友");
    }

    public static SlotsException emptyLike() {
        return new SlotsException(EC_EMPTY_LIKE, "没有可以领取的被点赞奖励");
    }

}
