/*
 * Created 2018-10-20 17:10:24
 */
package cn.com.yting.kxy.web.game.minearena;

import java.util.List;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class MineArenaLogComplex {

    private List<PitPositionChangeLog> pitPositionChangeLogs;
    private List<MineArenaRewardObtainLog> mineArenaRewardObtainLogs;
    private List<MineArenaChallengeLog> mineArenaChallengeLogs;
}
