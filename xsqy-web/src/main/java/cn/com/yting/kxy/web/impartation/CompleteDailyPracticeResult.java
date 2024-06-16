/*
 * Created 2018-11-24 18:43:23
 */
package cn.com.yting.kxy.web.impartation;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class CompleteDailyPracticeResult {

    private DailyPracticeRecord dailyPracticeRecord;
    private boolean completed;
}
