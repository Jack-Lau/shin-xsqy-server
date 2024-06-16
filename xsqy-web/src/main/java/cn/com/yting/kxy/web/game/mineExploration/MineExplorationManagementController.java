/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.mineExploration;

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
@RequestMapping("/management/mineExploration")
@ModuleDoc(moduleName = "mineExplorationManagement")
public class MineExplorationManagementController {

    @Autowired
    MineExplorationService mineExplorationService;

    @RequestMapping("/dailyReset")
    @WebInterfaceDoc(name = "dailyReset", description = "每日重置", response = "OK")
    public Object dailyReset() {
        mineExplorationService.dailyReset();
        return WebMessageWrapper.ok();
    }

    @RequestMapping("/end")
    @WebInterfaceDoc(name = "end", description = "活动结束时清理未领取的奖励", response = "OK")
    public Object end() {
        mineExplorationService.end();
        return WebMessageWrapper.ok();
    }

}
