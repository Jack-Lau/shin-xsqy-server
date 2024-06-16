/*
 * Created 2018-11-16 15:31:23
 */
package cn.com.yting.kxy.web.auction;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class CommodityDetail {

    private Commodity commodity;
    private int likeCount;
    private int bidderCount;
}
