/*
 * Created 2018-10-20 12:33:33
 */
package cn.com.yting.kxy.web.game.minearena;

import lombok.Value;

/**
 * 闭区间
 *
 * @author Azige
 */
@Value
public class RankingRange {

    private long lowerBound;
    private long upperBound;

    public RankingRange(long lowerBound, long upperBound) {
        if (lowerBound > upperBound) {
            throw new IllegalArgumentException("无效的参数，lowerBound=" + lowerBound + ", upperBound=" + upperBound);
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public boolean isInRange(long value) {
        return value >= lowerBound && value <= upperBound;
    }
}
