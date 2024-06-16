/*
 * Created 2018-10-19 11:43:40
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
public class MineArenaComplex {

    private MineArenaRecord mineArenaRecord;
    private PitDetail pit;
}
