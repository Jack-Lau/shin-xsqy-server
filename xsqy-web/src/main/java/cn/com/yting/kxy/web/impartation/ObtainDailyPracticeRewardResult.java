/*
 * Created 2018-11-22 11:38:07
 */
package cn.com.yting.kxy.web.impartation;

import cn.com.yting.kxy.web.award.AwardResult;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class ObtainDailyPracticeRewardResult {

    private DailyPracticeRecord dailyPracticeRecord;
    private AwardResult awardResult;
}
