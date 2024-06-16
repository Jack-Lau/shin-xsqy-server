/*
 * Created 2018-9-5 10:52:20
 */
package cn.com.yting.kxy.web.game.yibenwanli;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class YibenwanliOverrall {

    private long pool;
    private int ticketCount;
    private String lastPurchaserPlayerName;
    private Long timeToEnd;
    private long price;
    private boolean closed;
    private Long timeToNextSeason;
    private double lastShotRate;

}
