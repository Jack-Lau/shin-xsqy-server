/*
 * Created 2018-8-15 12:14:54
 */
package cn.com.yting.kxy.web.battle;

import cn.com.yting.kxy.battle.BattleResult;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class BattleResponse {

    private long battleSessionId;
    private BattleResult result;
}
