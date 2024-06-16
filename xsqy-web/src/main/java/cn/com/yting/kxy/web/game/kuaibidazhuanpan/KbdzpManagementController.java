/*
 * Created 2018-7-9 17:59:52
 */
package cn.com.yting.kxy.web.game.kuaibidazhuanpan;

import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/management/kbdzp")
public class KbdzpManagementController implements ModuleApiProvider {

    @Autowired
    private KbdzpSharedRepository kbdzpSharedRepository;
    @Autowired
    private KbdzpService kbdzpService;

    @RequestMapping("/view")
    public KbdzpSharedRecord viewSharedRecord() {
        return kbdzpSharedRepository.findById(KbdzpSharedRepository.DEFAULT_ID).get();
    }

    @RequestMapping("/fixRecords")
    public Object fixRecords() {
        kbdzpService.createRecordsForInexistent();
        return WebMessageWrapper.ok();
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .baseUri("/management/kbdzp")
            .name("块币大转盘管理")
            //
            .webInterface()
            .uri("/view")
            .description("查看块币大转盘的共享记录")
            .response(KbdzpSharedRecord.class, "块币大转盘的共享记录")
            .and()
            //
            .webInterface()
            .uri("/fixRecords")
            .description("为不存在记录的玩家生成记录")
            ;
    }
}
