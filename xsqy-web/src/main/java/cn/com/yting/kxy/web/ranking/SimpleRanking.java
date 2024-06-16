/*
 * Created 2018-11-2 12:09:14
 */
package cn.com.yting.kxy.web.ranking;

import java.util.List;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class SimpleRanking {

    private List<SimpleRankingRecord> topRecords;
    private List<SimpleRankingRecord> selfRecords;
}
