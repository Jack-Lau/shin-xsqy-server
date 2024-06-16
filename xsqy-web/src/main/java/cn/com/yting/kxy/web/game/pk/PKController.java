/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.pk;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/pk")
@ModuleDoc(moduleName = "切磋")
public class PKController {

    @Autowired
    PKService pkService;

    @PostMapping("/send")
    @WebInterfaceDoc(description = "发起切磋请求", response = "OK")
    public Object send(@AuthenticationPrincipal Account account, @RequestParam("receiverId") @ParamDoc("接收者的ID") long receiverId) {
        pkService.send(account.getId(), receiverId);
        return WebMessageWrapper.ok();
    }

    @PostMapping("/receive")
    @WebInterfaceDoc(description = "处理切磋请求", response = "OK")
    public Object receive(@AuthenticationPrincipal Account account, @RequestParam("senderId") @ParamDoc("发起者的ID") long senderId, @RequestParam("isOK") @ParamDoc("是否同意") boolean isOK) {
        pkService.receive(senderId, account.getId(), isOK);
        return WebMessageWrapper.ok();
    }

    @PostMapping("/async")
    @WebInterfaceDoc(description = "发起异步切磋", response = "OK")
    public Object async(@AuthenticationPrincipal Account account, @RequestParam("receiverId") @ParamDoc("接收者的ID") long receiverId) {
        pkService.async(account.getId(), receiverId);
        return WebMessageWrapper.ok();
    }

}
