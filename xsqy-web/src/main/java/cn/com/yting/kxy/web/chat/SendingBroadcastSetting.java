/*
 * Created 2018-10-23 11:53:12
 */
package cn.com.yting.kxy.web.chat;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class SendingBroadcastSetting {

    private String message;
    private long interval;
}
