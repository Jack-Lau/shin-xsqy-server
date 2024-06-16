/*
 * Created 2018-12-21 18:20:03
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class MjdhSeasonDetail {

    private MjdhSeason mjdhSeason;
    private int playerCount;
}
