/*
 * Created 2018-10-31 11:55:41
 */
package cn.com.yting.kxy.web.ranking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@Builder
@AllArgsConstructor
public class RankingValues {

    public static final RankingValues ZERO = new RankingValues(0, 0, 0, 0, 0);

    private long rankingValue_1;
    private long rankingValue_2;
    private long rankingValue_3;
    private long rankingValue_4;
    private long rankingValue_5;
}
