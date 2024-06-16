/*
 * Created 2018-12-12 13:15:42
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

import java.time.Instant;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class MjdhMatchRequest {

    private MjdhPlayerRecord playerRecord;
    private Instant eventTime;
}
