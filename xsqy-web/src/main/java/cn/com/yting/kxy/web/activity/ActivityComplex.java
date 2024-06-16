/*
 * Created 2018-10-16 19:00:38
 */
package cn.com.yting.kxy.web.activity;

import java.util.List;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class ActivityComplex {

    private ActivityPlayerRecord activityPlayerRecord;
    private List<ActivityRecord> activityRecords;
    private List<Long> openingActivityIds;

}
