/*
 * Created 2018-7-9 10:48:57
 */
package cn.com.yting.kxy.web.controller;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@ConditionalOnProperty("kxy.web.debug")
@RequestMapping("/debug")
public class DebugController implements ModuleApiProvider {

    @Autowired
    private TimeProvider timeProvider;

    @PostMapping(path = "/setTimeOffset")
    public Object setTimeOffset(
        @RequestParam("offset") long offset
    ) {
        timeProvider.setTimeOffset(offset);
        return WebMessageWrapper.ok();
    }

    @PostMapping(path = "/resetToSystemTime")
    public Object resetToSystemTime() {
        timeProvider.resetToSystemTime();
        return WebMessageWrapper.ok();
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .baseUri("/debug")
            .name("debug")
            //
            .webInterface()
            .uri("/setTimeOffset")
            .post()
            .description("设置时间偏移，用于调试")
            .requestParameter("integer", "offset", "时间的偏移量")
            .and()
            //
            .webInterface()
            .uri("/resetToSystemTime")
            .post()
            .description("重设时间为系统时间")
            .and();
    }
}
