/*
 * Created 2018-7-23 11:40:11
 */
package cn.com.yting.kxy.web.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.account.AccountRepository;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.mail.resource.MailInformation;
import groovy.text.GStringTemplateEngine;
import groovy.text.TemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class MailService implements ResetTask {

    @Autowired
    private MailRepository mailRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ResourceContext resourceContext;

    private final TemplateEngine templateEngine = new GStringTemplateEngine();

    public Mail sendMailTo(long accountId, String title, String content, List<CurrencyStack> attachmentList, int attachmentSource) {
        return sendMailToGroup(Collections.singletonList(accountId), title, content, attachmentList, attachmentSource).get(0);
    }

    public List<Mail> sendMailToGroup(List<Long> accountIds, String title, String content, List<CurrencyStack> attachmentList, int attachmentSource) {
        List<Mail> mails = new ArrayList<>();
        String attachmentText = CurrencyStack.listToText(attachmentList);
        for (long accountId : accountIds) {
            mails.add(createMail(accountId, title, content, attachmentText, attachmentSource));
        }

        mails = mailRepository.saveAll(mails);
        mailRepository.flush();

        eventPublisher.publishEvent(new MailSentEvent(title, false, mails));

        return mails;
    }

    public Mail sendMailToAll(String title, String content, List<CurrencyStack> attachmentList, int attachmentSource) {
        List<Mail> mails = new ArrayList<>();
        String attachmentText = CurrencyStack.listToText(attachmentList);
        for (long accountId : accountRepository.getAllIds()) {
            mails.add(createMail(accountId, title, content, attachmentText, attachmentSource));
        }

        mails = mailRepository.saveAll(mails);
        mailRepository.flush();

        Mail dummyMail = createMail(0, title, content, attachmentText, attachmentSource);
        eventPublisher.publishEvent(new MailSentEvent(title, true, Collections.singletonList(dummyMail)));
        return dummyMail;
    }

    public List<Mail> sendByRequest(MailSendingRequest request) {
        String title;
        String content;
        if (request.isUseTemplate()) {
            MailInformation mailInformation = MailInformation.getFrom(resourceContext, request.getTemplateId());
            mailInformation.createTemplateIfNull(templateEngine);
            title = mailInformation.getTitleTemplate().make(request.getParams()).toString();
            content = mailInformation.getContentTemplate().make(request.getParams()).toString();
        } else {
            title = request.getTitle();
            content = request.getContent();
        }

        if (request.isSendToAll()) {
            return Collections.singletonList(sendMailToAll(title, content, request.getAttachmentList(), request.getAttachmentSource()));
        } else {
            return sendMailToGroup(request.getAccountIds(), title, content, request.getAttachmentList(), request.getAttachmentSource());
        }
    }

    private Mail createMail(long accountId, String title, String content, String attachmentText, int attachmentSource) {
        Mail mail = new Mail();
        mail.setAccountId(accountId);
        mail.setTitle(title);
        mail.setContent(content);
        mail.setCreateTime(new Date(timeProvider.currentTime()));
        mail.setAttachment(attachmentText);
        mail.setAttachmentSource(attachmentSource);
        // 如果附件为空，则直接视为已领取
        if (attachmentText.isEmpty()) {
            mail.setAttachmentDelivered(true);
        }
        return mail;
    }

    public Mail markAlreadyRead(long accountId, long mailId) {
        Mail mail = findMailAndVerifyOwner(accountId, mailId);
        mail.setAlreadyRead(true);
        return mailRepository.save(mail);
    }

    public Mail obtainAttachment(long accountId, long mailId) {
        Mail mail = findMailAndVerifyOwner(accountId, mailId);
        if (mail.isAttachmentDelivered()) {
            throw MailException.attachmentAlreadyDelivered();
        }

        List<CurrencyStack> currencyStacks = mail.getAttachmentAsCurrencyStacks();
        for (CurrencyStack currencyStack : currencyStacks) {
            currencyService.increaseCurrency(accountId, currencyStack.getCurrencyId(), currencyStack.getAmount(), mail.getAttachmentSource());
        }

        mail.setAttachmentDelivered(true);
        return mailRepository.save(mail);
    }

    public void deleteMail(long accountId, long mailId) {
        Mail mail = findMailAndVerifyOwner(accountId, mailId);
        mailRepository.delete(mail);
    }

    public void deleteNeedlessMail(long accountId) {
        mailRepository.deleteNeedlessByAccountId(accountId);
    }

    private Mail findMailAndVerifyOwner(long accountId, long mailId) {
        Mail mail = mailRepository.findByIdForWrite(mailId).orElseThrow(() -> KxyWebException.notFound("邮件不存在，id=" + mailId));
        if (mail.getAccountId() != accountId) {
            throw MailException.notOwner();
        }
        return mail;
    }

    @Override
    public void dailyReset() {
        mailRepository.deleteExpired(Date.from(timeProvider.currentOffsetDateTime().minusDays(MailConstants.EXPIRE_DAYS).toInstant()));
    }
}
