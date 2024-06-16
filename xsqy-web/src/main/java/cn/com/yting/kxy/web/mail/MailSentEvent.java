/*
 * Created 2018-7-23 12:05:13
 */
package cn.com.yting.kxy.web.mail;

import java.util.List;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class MailSentEvent extends KxyWebEvent {

    private final boolean needBroadcast;
    private final List<Mail> mails;

    public MailSentEvent(Object source, boolean needBroadcast, List<Mail> mails) {
        super(source);
        this.needBroadcast = needBroadcast;
        this.mails = mails;
    }

    /**
     * 表示这是否是一个需要广播的邮件发送事件
     * @return
     */
    public boolean isNeedBroadcast() {
        return needBroadcast;
    }

    /**
     * 此邮件发送事件关联的邮件列表，如果 {@link #isNeedBroadcast() } 为 true，
     * 则列表中有且只有一个 id 和 accountId 均为 0 的邮件对象
     *
     * @return
     */
    public List<Mail> getMails() {
        return mails;
    }
}
