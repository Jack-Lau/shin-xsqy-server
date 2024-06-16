/*
 * Created 2018-7-13 16:39:33
 */
package cn.com.yting.kxy.web.game.kuaibidazhuanpan;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class KbdzpAwardLog {

    private String playerName;
    private long kbdzpAwardId;
    private long eventTime;
}
