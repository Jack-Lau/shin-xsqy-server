/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.work;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/work")
@ModuleDoc(moduleName = "打工")
public class WorkController {

    @Autowired
    WorkService workService;

    @PostMapping("/update")
    @WebInterfaceDoc(description = "推进打工进度", response = "打工记录")
    public WorkRecord update(@AuthenticationPrincipal Account account) {
        return workService.update(account.getId());
    }
    
    @PostMapping("/start")
    @WebInterfaceDoc(description = "开始打工", response = "打工记录")
    public WorkRecord start(@AuthenticationPrincipal Account account) {
        return workService.start(account.getId());
    }
    
    @PostMapping("/end")
    @WebInterfaceDoc(description = "结束打工", response = "打工记录")
    public WorkRecord end(@AuthenticationPrincipal Account account) {
        return workService.end(account.getId());
    }

}
