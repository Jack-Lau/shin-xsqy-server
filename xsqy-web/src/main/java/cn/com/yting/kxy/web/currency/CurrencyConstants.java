/*
 * Created 2018-7-12 17:28:25
 */
package cn.com.yting.kxy.web.currency;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Azige
 */
public final class CurrencyConstants {

    public static final long ID_元宝 = 150;
    public static final long ID_毫仙石 = 151;
    public static final long ID_能量 = 152;
    public static final long ID_经验 = 153;
    public static final long ID_强化石 = 154;
    public static final long ID_活跃点 = 155;
    public static final long ID_贡献值 = 156;
    public static final long ID_舍利碎片 = 157;
    public static final long ID_积分 = 159;
    public static final long ID_灵宠要诀 = 160;
    public static final long ID_强化保护卡 = 161;
    public static final long ID_玉石 = 162;
    public static final long ID_师徒值 = 163;
    public static final long ID_斗战点 = 164;
    public static final long ID_荣誉 = 165;
    public static final long ID_染色剂 = 166;
    public static final long ID_改名卡 = 167;
    public static final long ID_九灵仙丹 = 168;
    public static final long ID_代金券 = 169;
    public static final long ID_拜年红包1 = 170;
    public static final long ID_拜年红包2 = 171;
    public static final long ID_神兽精魄 = 172;
    public static final long ID_好友代金券 = 173;
    public static final long ID_坊金 = 174;
    public static final long ID_金坷垃 = 185;
    public static final long ID_钓鱼点 = 191;
    public static final long ID_长乐贡牌 = 194;
    public static final long ID_魂晶 = 195;
    public static final long ID_门贡 = 196;

    public static final long ID_藏宝图 = 20001;

    public static final int PURPOSE_INCREMENT_未指定块币产出源 = -1;
    public static final int PURPOSE_INCREMENT_充值 = 1000;
    public static final int PURPOSE_INCREMENT_块币大转盘 = 1001;
    public static final int PURPOSE_INCREMENT_任务产生的奖励 = 1002;
    public static final int PURPOSE_INCREMENT_一本万利奖金 = 1003;
    public static final int PURPOSE_INCREMENT_提现失败退还 = 1004;
    public static final int PURPOSE_INCREMENT_提现失败退还手续费 = 1005;
    public static final int PURPOSE_INCREMENT_邮件_未指定块币附件来源 = 1006;
    public static final int PURPOSE_INCREMENT_邀请回报 = 1007;
    public static final int PURPOSE_INCREMENT_金光塔奖金 = 1008;
    public static final int PURPOSE_INCREMENT_礼包兑换 = 1009;
    public static final int PURPOSE_INCREMENT_抢占摇钱树产出 = 1010;
    public static final int PURPOSE_INCREMENT_装备提取失败退还手续费 = 1011;
    public static final int PURPOSE_INCREMENT_宝图 = 1012;
    public static final int PURPOSE_INCREMENT_战力排行榜奖励 = 1013;
    public static final int PURPOSE_INCREMENT_卖出古董 = 1014;
    public static final int PURPOSE_INCREMENT_古董全服奖励 = 1015;
    public static final int PURPOSE_INCREMENT_宠物提取失败退还手续费 = 1016;
    public static final int PURPOSE_INCREMENT_拍卖取出元宝 = 1017;
    public static final int PURPOSE_INCREMENT_拍卖点赞分红 = 1018;
    public static final int PURPOSE_INCREMENT_使用消耗品 = 1019;
    public static final int PURPOSE_INCREMENT_师徒系统分红 = 1020;
    public static final int PURPOSE_INCREMENT_神秘商店抽奖 = 1021;
    public static final int PURPOSE_INCREMENT_神秘商店兑换 = 1022;
    public static final int PURPOSE_INCREMENT_兑换商店产出 = 1023;
    public static final int PURPOSE_INCREMENT_乱斗大会产出 = 1024;
    public static final int PURPOSE_INCREMENT_西游摇翻天产出 = 1025;
    public static final int PURPOSE_INCREMENT_名剑大会1V1排行榜 = 1026;
    public static final int PURPOSE_INCREMENT_交易行出售货款 = 1027;
    public static final int PURPOSE_INCREMENT_矿山探宝奖励 = 1028;
    public static final int PURPOSE_INCREMENT_长乐坊兑换块币 = 1029;
    public static final int PURPOSE_INCREMENT_欢乐筒筒返还下注 = 1030;
    public static final int PURPOSE_INCREMENT_欢乐筒筒胜利 = 1031;
    public static final int PURPOSE_INCREMENT_看广告 = 1032;
    public static final int PURPOSE_INCREMENT_购买钓竿 = 1033;
    public static final int PURPOSE_INCREMENT_钓鱼 = 1034;
    public static final int PURPOSE_INCREMENT_红包六六六 = 1035;
    public static final int PURPOSE_INCREMENT_长乐聚宝盆 = 1036;

    public static final List<Integer> PURPOSE_INCREMENT_FROM_AIRDROP = Arrays.asList(
            1001,
            1002,
            1003,
            1005,
            1006,
            1008,
            1009,
            1010,
            1011,
            1012,
            1014,
            1015,
            1016,
            1018,
            1019,
            1021,
            1022,
            1023,
            1024,
            1025,
            1026,
            1028,
            1029,
            1031);

    public static final List<Integer> PURPOSE_INCREMENT_FROM_REBATE = Arrays.asList(
            1007,
            1013,
            1020);

    public static final List<Integer> PURPOSE_INCREMENT_FROM_TRANSFER = Arrays.asList(
            1000,
            1004,
            1017,
            1027,
            1030,
            1034,
            1035);

    public static final int PURPOSE_DECREMENT_提现 = 2000;
    public static final int PURPOSE_DECREMENT_提现手续费 = 2001;
    public static final int PURPOSE_DECREMENT_一本万利购买本票 = 2002;
    public static final int PURPOSE_DECREMENT_提升邀请上限 = 2003;
    public static final int PURPOSE_DECREMENT_装备打造 = 2004;
    public static final int PURPOSE_DECREMENT_提交货币的任务 = 2005;
    public static final int PURPOSE_DECREMENT_获得宠物 = 2006;
    public static final int PURPOSE_DECREMENT_抢占摇钱树挑战费用 = 2007;
    public static final int PURPOSE_DECREMENT_装备提取手续费 = 2008;
    public static final int PURPOSE_DECREMENT_三界经商雇佣商队 = 2009;
    public static final int PURPOSE_DECREMENT_三界经商提升经商位上限 = 2010;
    public static final int PURPOSE_DECREMENT_购买_修复古董 = 2011;
    public static final int PURPOSE_DECREMENT_宠物提取手续费 = 2012;
    public static final int PURPOSE_DECREMENT_拍卖存入元宝 = 2013;
    public static final int PURPOSE_DECREMENT_拍卖支付元宝 = 2014;
    public static final int PURPOSE_DECREMENT_神秘商店抽奖 = 2015;
    public static final int PURPOSE_DECREMENT_神秘商店兑换 = 2016;
    public static final int PURPOSE_DECREMENT_兑换商店购买 = 2017;
    public static final int PURPOSE_DECREMENT_装备重铸 = 2018;
    public static final int PURPOSE_DECREMENT_西游摇翻天 = 2019;
    public static final int PURPOSE_DECREMENT_支付交易行货品 = 2020;
    public static final int PURPOSE_DECREMENT_交易行手续费 = 2021;
    public static final int PURPOSE_DECREMENT_矿山探宝续命 = 2022;
    public static final int PURPOSE_DECREMENT_长乐坊购买本票 = 2023;
    public static final int PURPOSE_DECREMENT_欢乐筒筒下注 = 2024;
    public static final int PURPOSE_DECREMENT_欢乐筒筒支付 = 2025;
    public static final int PURPOSE_DECREMENT_购买钓竿 = 2026;
    public static final int PURPOSE_DECREMENT_钓鱼 = 2027;
    public static final int PURPOSE_DECREMENT_红包六六六报名 = 2028;
    public static final int PURPOSE_DECREMENT_红包六六六支付 = 2029;
    public static final int PURPOSE_DECREMENT_长乐聚宝盆 = 2030;
    public static final int PURPOSE_DECREMENT_装备附魂 = 2031;
    public static final int PURPOSE_DECREMENT_宠物附魂 = 2032;
    public static final int PURPOSE_DECREMENT_使用消耗品 = 2033;
    public static final int PURPOSE_DECREMENT_抢占摇钱树领取奖励 = 2034;

    public static final List<Integer> PURPOSE_DECREMENT_FROM_ACTURAL_COST = Arrays.asList(
            2001,
            2003,
            2004,
            2005,
            2006,
            2007,
            2008,
            2009,
            2010,
            2012,
            2014,
            2015,
            2016,
            2017,
            2018,
            2021,
            2031,
            2032,
            2033,
            2034
    );

    public static final List<Integer> PURPOSE_DECREMENT_FROM_PLAYER_INTERACTIVE = Arrays.asList(
            2002,
            2011,
            2019,
            2022,
            2023,
            2025,
            2029
    );

    public static final List<Integer> PURPOSE_DECREMENT_FROM_TRANSFER = Arrays.asList(
            2000,
            2013,
            2020,
            2024,
            2026,
            2028
    );

    private CurrencyConstants() {
    }

}
