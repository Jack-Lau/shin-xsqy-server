/*
 * Created 2018-11-16 15:31:03
 */
package cn.com.yting.kxy.web.auction;

import java.util.List;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class AuctionOverall {

    private AuctionRecord auctionRecord;
    private List<CommodityDetail> onSaleCommodities;
    private List<CommodityPlayerRecord> commodityPlayerRecords;
}
