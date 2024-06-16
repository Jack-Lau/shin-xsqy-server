/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.changlefang;

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
@RequestMapping("/management/changlefang")
@ModuleDoc(moduleName = "changlefangManagement")
public class ChanglefangManagementController {

    @Autowired
    ChanglefangService changlefangService;

    @RequestMapping("/dailyReset")
    @WebInterfaceDoc(name = "dailyReset", description = "每日重置", response = "OK")
    public Object dailyReset() {
        changlefangService.dailyReset();
        return WebMessageWrapper.ok();
    }

}
