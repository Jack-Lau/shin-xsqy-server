/*
 * Created 2018-7-23 16:07:24
 */
package cn.com.yting.kxy.web.mail;

import java.util.List;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import cn.com.yting.kxy.web.message.WebsocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/mail")
public class MailController implements ModuleApiProvider {

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private WebsocketMessageService websocketMessageService;

    @RequestMapping("/view/mine")
    public List<Mail> viewMine(
        @AuthenticationPrincipal Account account,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return mailRepository.findByAccountIdOrderByIdDesc(account.getId(), PageRequest.of(page, size));
    }

    @RequestMapping("/existsUnread")
    public boolean existsUnread(@AuthenticationPrincipal Account account) {
        return mailRepository.existsByAccountIdAndUnread(account.getId());
    }

    @PostMapping("/action/{mailId}/markAlreadyRead")
    public Mail markAlreadyRead(
        @AuthenticationPrincipal Account account,
        @PathVariable("mailId") long mailId
    ) {
        return mailService.markAlreadyRead(account.getId(), mailId);
    }

    @PostMapping("/action/{mailId}/obtainAttachment")
    public Mail obtainAttachment(
        @AuthenticationPrincipal Account account,
        @PathVariable("mailId") long mailId
    ) {
        return mailService.obtainAttachment(account.getId(), mailId);
    }

    @PostMapping("/action/{mailId}/delete")
    public WebMessageWrapper delete(
        @AuthenticationPrincipal Account account,
        @PathVariable("mailId") long mailId
    ) {
        mailService.deleteMail(account.getId(), mailId);
        return WebMessageWrapper.ok();
    }

    @PostMapping("/deleteNeedless")
    public WebMessageWrapper deleteNeedless(@AuthenticationPrincipal Account account) {
        mailService.deleteNeedlessMail(account.getId());
        return WebMessageWrapper.ok();
    }

    @TransactionalEventListener
    public void onMailSent(MailSentEvent event) {
        if (event.isNeedBroadcast()) {
            websocketMessageService.sendToAll("/mail/mailSent", event.getMails().get(0));
        } else {
            event.getMails().forEach(mail -> {
                websocketMessageService.sendToUser(mail.getAccountId(), "/mail/mailSent", mail);
            });
        }
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .name("mail")
            .baseUri("/mail")
            //
            .webInterface()
            .name("viewMine")
            .uri("/view/mine")
            .description("获得所有的自己的邮件")
            .requestParameter("integer", "page", "分页编号，从 0 开始")
            .requestParameter("integer", "size", "分页大小")
            .responseArray(Mail.class, "对应的邮件")
            .and()
            //
            .webInterface()
            .name("existsUnread")
            .uri("/existsUnread")
            .description("检查是否有未读邮件")
            .response("boolean", "true 为有未读邮件")
            .and()
            //
            .webInterface()
            .name("markAlreadyRead")
            .uri("/action/{mailId}/markAlreadyRead")
            .post()
            .description("标记一个邮件为已读")
            .requestParameter("integer", "mailId", "邮件id")
            .response(Mail.class, "修改后的邮件")
            .expectableError(MailException.EC_NOT_OWNER, "不是邮件的所有者")
            .and()
            //
            .webInterface()
            .name("obtainAttachment")
            .uri("/action/{mailId}/obtainAttachment")
            .post()
            .description("领取一个邮件的附件")
            .requestParameter("integer", "mailId", "邮件id")
            .response(Mail.class, "修改后的邮件")
            .expectableError(MailException.EC_NOT_OWNER, "不是邮件的所有者")
            .expectableError(MailException.EC_ATTACHMENT_ALREADY_DELIVERED, "已经领取过附件")
            .and()
            //
            .webInterface()
            .name("delete")
            .uri("/action/{mailId}/delete")
            .post()
            .description("删除一个邮件")
            .requestParameter("integer", "mailId", "邮件id")
            .expectableError(MailException.EC_NOT_OWNER, "不是邮件的所有者")
            .and()
            //
            .webInterface()
            .name("deleteNeedless")
            .uri("/deleteNeedless")
            .post()
            .description("删除所有已读已领取附件的邮件")
            .and()
            //
            //
            //
            .webNotification()
            .description("对单个用户发送邮件时的通知")
            .queue("/mail/mailSent")
            .messageType(Mail.class)
            .and()
            //
            .webNotification()
            .description("群发邮件时的通知（此时邮件中的id和accountId均为0）")
            .topic("/mail/mailSent")
            .messageType(Mail.class)
            .and();
    }
}
