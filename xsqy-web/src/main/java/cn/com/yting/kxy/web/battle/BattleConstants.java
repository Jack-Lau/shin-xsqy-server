/*
 * Created 2018-9-27 17:33:02
 */
package cn.com.yting.kxy.web.battle;

import java.util.Map;

import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.robots.SchoolRobot_100_NonPlayer;
import cn.com.yting.kxy.battle.robots.SchoolRobot_100_Player;
import cn.com.yting.kxy.battle.robots.SchoolRobot_101_NonPlayer;
import cn.com.yting.kxy.battle.robots.SchoolRobot_101_Player;
import cn.com.yting.kxy.battle.robots.SchoolRobot_102_NonPlayer;
import cn.com.yting.kxy.battle.robots.SchoolRobot_102_Player;
import cn.com.yting.kxy.battle.robots.SchoolRobot_103_NonPlayer;
import cn.com.yting.kxy.battle.robots.SchoolRobot_103_Player;
import cn.com.yting.kxy.battle.robots.SchoolRobot_104_NonPlayer;
import cn.com.yting.kxy.battle.robots.SchoolRobot_104_Player;
import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Azige
 */
public final class BattleConstants {

    /**
     * 是否玩家角色 -> 门派id -> Robot 的映射
     */
    public static final Map<Boolean, Map<Long, Robot>> ROBOT_MAP = ImmutableMap.of(
            true, ImmutableMap.of(
                    100L, new SchoolRobot_100_Player(),
                    101L, new SchoolRobot_101_Player(),
                    102L, new SchoolRobot_102_Player(),
                    103L, new SchoolRobot_103_Player(),
                    104L, new SchoolRobot_104_Player()
            ),
            false, ImmutableMap.of(
                    100L, new SchoolRobot_100_NonPlayer(),
                    101L, new SchoolRobot_101_NonPlayer(),
                    102L, new SchoolRobot_102_NonPlayer(),
                    103L, new SchoolRobot_103_NonPlayer(),
                    104L, new SchoolRobot_104_NonPlayer()
            )
    );

    public static final int PET_POSITION_OFFSET = 3;

    private BattleConstants() {
    }

}
