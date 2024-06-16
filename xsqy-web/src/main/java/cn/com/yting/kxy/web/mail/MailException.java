/*
 * Created 2018-7-23 15:24:09
 */
package cn.com.yting.kxy.web.mail;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class MailException extends KxyWebException {

    public static final int EC_NOT_OWNER = 500;
    public static final int EC_ATTACHMENT_ALREADY_DELIVERED = 501;

    public MailException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static MailException notOwner() {
        return new MailException(EC_NOT_OWNER, "不是邮件的所有者");
    }

    public static MailException attachmentAlreadyDelivered() {
        return new MailException(EC_ATTACHMENT_ALREADY_DELIVERED, "已经领取过附件");
    }
}
