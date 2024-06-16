/*
 * Created 2018-7-12 14:46:03
 */
package cn.com.yting.kxy.web.invitation;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@WebMessageType
@Value
public class InvitationInfo {

    private InviterRecord inviterRecord;
    private int invitationCount;
}
