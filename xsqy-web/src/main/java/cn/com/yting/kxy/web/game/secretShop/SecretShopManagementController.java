/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.secretShop;

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
@RequestMapping("/management/secretShop")
@ModuleDoc(moduleName = "secretShopManagement")
public class SecretShopManagementController {

    @Autowired
    SecretShopService secretShopService;

    @RequestMapping("/dailyReset")
    @WebInterfaceDoc(name = "dailyReset", description = "每日重置", response = "OK")
    public Object dailyReset() {
        secretShopService.dailyReset();
        return WebMessageWrapper.ok();
    }

}
