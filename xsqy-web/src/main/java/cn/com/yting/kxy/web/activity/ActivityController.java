/*
 * Created 2018-10-16 18:57:20
 */
package cn.com.yting.kxy.web.activity;

import java.util.List;

import cn.com.yting.kxy.core.DebugOnly;
import cn.com.yting.kxy.core.resetting.ResetType;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/activity")
public class ActivityController implements ModuleApiProvider {

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ActivityPlayerRepository activityPlayerRepository;

    @Autowired
    private ActivityService activityService;

    @RequestMapping("/overall")
    public ActivityComplex overall(@AuthenticationPrincipal Account account) {
        ActivityPlayerRecord activityPlayerRecord = activityPlayerRepository.findOrCreateById(account.getId());
        List<ActivityRecord> activityRecords = activityRepository.findByAccountId(account.getId());
        return new ActivityComplex(activityPlayerRecord, activityRecords, activityService.getOpeningActivityIds());
    }

    @DebugOnly
    @PostMapping("/triggerDailyReset")
    public WebMessageWrapper triggerDailyReset() {
        activityService.anyReset(ResetType.DAILY);
        activityService.dailyReset();
        return WebMessageWrapper.ok();
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
                .name("activity")
                .baseUri("/activity")
                //
                .webInterface()
                .uri("/overall")
                .description("查询活动总览信息")
                .response(ActivityComplex.class, "复合的活动相关信息")
                .and()
                //
                .webInterface()
                .uri("/triggerDailyReset")
                .post()
                .description("触发每日重置（测试用）")
                .and();
    }
}
