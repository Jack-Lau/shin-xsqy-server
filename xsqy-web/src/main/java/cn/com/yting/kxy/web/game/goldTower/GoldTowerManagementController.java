/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower;

import cn.com.yting.kxy.web.apimodel.Module;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Darkholme
 */
@RestController
@RequestMapping("/management/goldTower")
public class GoldTowerManagementController implements ModuleApiProvider {

    @Autowired
    GoldTowerService goldTowerService;

    @RequestMapping("/dailyReset")
    public Object dailyReset() {
        goldTowerService.dailyReset();
        return WebMessageWrapper.ok();
    }

    @Override
    public void buildModuleApi(Module.ModuleBuilder<?> builder) {
        builder
                .baseUri("/management/goldTower")
                .name("金光塔管理")
                //
                .webInterface()
                .uri("/dailyReset")
                .description("每日结算");
    }

}
