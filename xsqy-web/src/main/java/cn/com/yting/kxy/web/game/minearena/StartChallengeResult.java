/*
 * Created 2018-10-20 13:01:51
 */
package cn.com.yting.kxy.web.game.minearena;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class StartChallengeResult {

    private long battleSessionId;
}
