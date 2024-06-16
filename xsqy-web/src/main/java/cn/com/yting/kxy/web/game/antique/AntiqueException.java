/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.antique;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Darkholme
 */
public class AntiqueException extends KxyWebException {

    public static final int EC_NOT_STARTED_YET = 1800;
    public static final int EC_INSUFFICIENT_PLAYER_LEVEL = 1801;
    public static final int EC_INSUFFICIENT_CURRENCY = 1802;
    public static final int EC_ALREADY_IN_REPAIR = 1803;
    public static final int EC_DO_NOT_HAVE_ANTIQUE = 1804;
    public static final int EC_ANTIQUE_LEVEL_MAX = 1805;
    public static final int EC_AWARD_TAKEN = 1806;
    public static final int EC_AWARD_TAKE_COUNT_MAX = 1807;
    public static final int EC_INSUFFICIENT_AWARD_REMAIN = 1808;
    public static final int EC_修复了同一个部位 = 1809;

    public AntiqueException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static AntiqueException notStartedYet() {
        return new AntiqueException(EC_NOT_STARTED_YET, "活动未开启");
    }

    public static AntiqueException insufficientPlayerLevel() {
        return new AntiqueException(EC_INSUFFICIENT_PLAYER_LEVEL, "角色等级不足");
    }

    public static AntiqueException insufficientCurrency() {
        return new AntiqueException(EC_INSUFFICIENT_CURRENCY, "拥有的货币不足");
    }

    public static AntiqueException alreadyInRepair() {
        return new AntiqueException(EC_ALREADY_IN_REPAIR, "已经在修复一个古董");
    }

    public static AntiqueException doNotHaveAntique() {
        return new AntiqueException(EC_DO_NOT_HAVE_ANTIQUE, "当前没拥有古董");
    }

    public static AntiqueException antiqueLevelMax() {
        return new AntiqueException(EC_ANTIQUE_LEVEL_MAX, "古董已达最高修复等级");
    }

    public static AntiqueException awardTaken() {
        return new AntiqueException(EC_AWARD_TAKEN, "该全服奖励已经领过了");
    }

    public static AntiqueException awardTakeCountMax() {
        return new AntiqueException(EC_AWARD_TAKE_COUNT_MAX, "今日已达到领取次数上限");
    }

    public static AntiqueException insufficientAwardRemain() {
        return new AntiqueException(EC_INSUFFICIENT_AWARD_REMAIN, "全服奖励的剩余数量不足");
    }

    public static AntiqueException 修复了同一个部位() {
        return new AntiqueException(EC_修复了同一个部位, "修复了同一个部位");
    }

}
