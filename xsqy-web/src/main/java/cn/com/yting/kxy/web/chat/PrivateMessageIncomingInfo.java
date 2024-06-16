/*
 * Created 2018-8-6 18:35:03
 */
package cn.com.yting.kxy.web.chat;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class PrivateMessageIncomingInfo {

    private long senderAccountId;
    private long count;
    
}
