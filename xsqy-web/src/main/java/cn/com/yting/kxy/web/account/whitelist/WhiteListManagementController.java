/*
 * Created 2018-9-25 11:33:47
 */
package cn.com.yting.kxy.web.account.whitelist;

import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RequestMapping("/management/whiteList")
@RestController
public class WhiteListManagementController implements ModuleApiProvider {

    @Autowired
    private WhiteListStatusHolder whiteListStatusHolder;

    @GetMapping("/status")
    public boolean getStatus() {
        return whiteListStatusHolder.isEnabled();
    }

    @PostMapping("/status")
    public boolean setStatus(@RequestParam("value") boolean value) {
        whiteListStatusHolder.setEnabled(value);
        return whiteListStatusHolder.isEnabled();
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .name("白名单管理")
            .baseUri("/management/whiteList")
            //
            .webInterface()
            .name("getStatus")
            .uri("/status")
            .description("获得当前白名单启用状态")
            .response("boolean", "白名单启用状态，true 为启用")
            .and()
            //
            .webInterface()
            .name("setStatus")
            .uri("/status")
            .post()
            .description("设置当前白名单启用状态")
            .requestParameter("boolean", "value", "白名单启用状态")
            .response("boolean", "白名单启用状态，true 为启用")
            .and();
    }
}
