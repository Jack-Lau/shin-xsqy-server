/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.baccarat;

import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Darkholme
 */
@RestController
@RequestMapping("/management/baccarat")
@ModuleDoc(moduleName = "baccaratManagement")
public class BaccaratManagementController {

    @Autowired
    BaccaratService baccaratService;

    @RequestMapping("/open")
    @WebInterfaceDoc(name = "open", description = "开启", response = "OK")
    public Object open() {
        baccaratService.open();
        return WebMessageWrapper.ok();
    }

    @RequestMapping("/close")
    @WebInterfaceDoc(name = "close", description = "关闭", response = "OK")
    public Object close() {
        baccaratService.close();
        return WebMessageWrapper.ok();
    }

}
