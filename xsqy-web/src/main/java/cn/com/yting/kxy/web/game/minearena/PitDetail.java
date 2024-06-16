/*
 * Created 2018-10-19 12:34:48
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
public class PitDetail {

    private Pit pit;
    private boolean locked;
    private long currencyId;
    private double factor;

}
