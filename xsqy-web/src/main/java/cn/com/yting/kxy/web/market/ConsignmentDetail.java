/*
 * Created 2018-12-25 16:39:43
 */
package cn.com.yting.kxy.web.market;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class ConsignmentDetail {

    private Consignment consignment;
    private long markerCount;
}
