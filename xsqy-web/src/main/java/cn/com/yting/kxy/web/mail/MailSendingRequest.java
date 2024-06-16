/*
 * Created 2018-7-25 17:13:05
 */
package cn.com.yting.kxy.web.mail;

import cn.com.yting.kxy.web.currency.CurrencyConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.com.yting.kxy.web.currency.CurrencyStack;
import lombok.Data;

/**
 * 描述发送邮件的请求的类型
 *
 * @author Azige
 */
@Data
public class MailSendingRequest {

    /**
     * 发送的目标的账号 id 列表
     */
    private List<Long> accountIds = new ArrayList<>();
    /**
     * 此邮件是否发送给所有人，如果为 true 则 accountIds 应当被忽略
     */
    private boolean sendToAll;

    /**
     * 邮件标题
     */
    private String title;
    /**
     * 邮件正文
     */
    private String content;
    /**
     * 是否使用模版，如果为 true 则 title 和 content 应当被忽略而使用 templateId 和 params
     */
    private boolean useTemplate;
    /**
     * 模版的 id
     */
    private long templateId;
    /**
     * 模版的参数集
     */
    private Map<String, String> params;

    /**
     * 附件的列表
     */
    private List<CurrencyStack> attachmentList = Collections.emptyList();

    /**
     * 附件的来源
     */
    private int attachmentSource = CurrencyConstants.PURPOSE_INCREMENT_邮件_未指定块币附件来源;

    public MailSendingRequest to(long accountId) {
        accountIds.add(accountId);
        return this;
    }

    public MailSendingRequest to(List<Long> accountIdList) {
        accountIds.addAll(accountIdList);
        return this;
    }

    public MailSendingRequest toAll() {
        sendToAll = true;
        return this;
    }

    public MailSendingRequest title(String title) {
        this.title = title;
        return this;
    }

    public MailSendingRequest content(String content) {
        this.content = content;
        return this;
    }

    public MailSendingRequest template(long templateId) {
        return template(templateId, Collections.emptyMap());
    }

    public MailSendingRequest template(long templateId, Map<String, String> params) {
        useTemplate = true;
        this.templateId = templateId;
        this.params = params;
        return this;
    }

    public MailSendingRequest attachment(List<CurrencyStack> attachmentList) {
        this.attachmentList = attachmentList;
        return this;
    }

    public MailSendingRequest attachment(String attachmentText) {
        this.attachmentList = CurrencyStack.listFromText(attachmentText);
        return this;
    }

    public MailSendingRequest attachmentSource(int attachmentSource) {
        this.attachmentSource = attachmentSource;
        return this;
    }

    public void commit(MailService mailService) {
        mailService.sendByRequest(this);
    }

    public static MailSendingRequest create() {
        return new MailSendingRequest();
    }
}
