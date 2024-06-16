/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.antique;

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
@RequestMapping("/management/antique")
public class AntiqueManagementController implements ModuleApiProvider {

    @Autowired
    AntiqueService antiqueService;

    @RequestMapping("/dailyReset")
    public Object dailyReset() {
        antiqueService.dailyReset();
        return WebMessageWrapper.ok();
    }

    @RequestMapping("/end")
    public Object end() {
        antiqueService.end();
        return WebMessageWrapper.ok();
    }

    @Override
    public void buildModuleApi(Module.ModuleBuilder<?> builder) {
        builder
                .baseUri("/management/antique")
                .name("西域商人管理")
                //
                .webInterface()
                .uri("/dailyReset")
                .description("每日结算")
                .and()
                //
                .webInterface()
                .uri("/end")
                .description("活动结束时清理未售出的古董");
    }

}
