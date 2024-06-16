/*
 * Created 2018-7-24 12:09:26
 */
package cn.com.yting.kxy.web.mail;

import java.util.List;

import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/management/mail")
public class MailManagementController implements ModuleApiProvider {

    @Autowired
    private MailService mailService;

    @PostMapping("/sendMailToOne")
    public Mail sendMailToOne(
            @RequestParam(name = "accountId") long accountId,
            @RequestParam(name = "title") String title,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "attachment") String attachmentText
    ) {
        return mailService.sendMailTo(accountId, title, content, CurrencyStack.listFromText(attachmentText), CurrencyConstants.PURPOSE_INCREMENT_邮件_未指定块币附件来源);
    }

    @PostMapping("/sendMailToAll")
    public Mail sendMailToAll(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "content") String content,
            @RequestParam(name = "attachment") String attachmentText
    ) {
        return mailService.sendMailToAll(title, content, CurrencyStack.listFromText(attachmentText), CurrencyConstants.PURPOSE_INCREMENT_邮件_未指定块币附件来源);
    }

    @PostMapping(path = "/sendMail", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Mail> sendMail(@RequestBody MailSendingRequest request) {
        return mailService.sendByRequest(request);
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
                .name("邮件管理")
                .baseUri("/management/mail")
                //
                .webInterface()
                .uri("/sendMailToOne")
                .post()
                .description("向单个用户发送邮件")
                .requestParameter("integer", "accountId", "发送目标的账号id")
                .requestParameter("string", "title", "标题")
                .requestParameter("string", "content", "正文")
                .requestParameter("string", "attachment", "附件，货币堆的文本形式")
                .response(Mail.class, "成功创建的邮件")
                .and()
                //
                .webInterface()
                .uri("/sendMailToAll")
                .post()
                .description("向所有人发邮件")
                .requestParameter("string", "title", "标题")
                .requestParameter("string", "content", "正文")
                .requestParameter("string", "attachment", "附件，货币堆的文本形式")
                .and();
    }

}
