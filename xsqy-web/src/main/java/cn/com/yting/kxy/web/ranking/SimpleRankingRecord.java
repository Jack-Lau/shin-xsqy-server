/*
 * Created 2018-11-2 12:07:16
 */
package cn.com.yting.kxy.web.ranking;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class SimpleRankingRecord {

    private long accountId;
    private long objectId;
    private int currentRank;
    private long rankValue;
}
