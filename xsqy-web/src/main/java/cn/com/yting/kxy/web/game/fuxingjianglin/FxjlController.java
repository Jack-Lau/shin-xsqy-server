/*
 * Created 2019-1-22 12:33:29
 */
package cn.com.yting.kxy.web.game.fuxingjianglin;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import cn.com.yting.kxy.web.award.AwardResult;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import cn.com.yting.kxy.web.quest.QuestRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/fxjl")
@ModuleDoc(moduleName = "福星降临")
public class FxjlController {

    @Autowired
    private FxjlSharedRepository fxjlSharedRepository;
    @Autowired
    private FxjlRepository fxjlRepository;

    @Autowired
    private FxjlService fxjlService;

    @RequestMapping("/overall")
    @WebInterfaceDoc(description = "查询总览信息", response = "总览信息")
    public FxjlOverall overall(@AuthenticationPrincipal Account account) {
        FxjlSharedRecord sharedRecord = fxjlSharedRepository.getTheRecord();
        FxjlRecord record = fxjlRepository.findById(account.getId()).orElse(null);
        return new FxjlOverall(sharedRecord, record);
    }

    @PostMapping("/createRecord")
    @WebInterfaceDoc(description = "创建福星降临记录", response = "福星降临记录")
    public FxjlRecord createRecord(@AuthenticationPrincipal Account account) {
        return fxjlService.createRecord(account.getId());
    }

    @PostMapping("/startQuest")
    @WebInterfaceDoc(description = "开始一个福星降临任务", response = "任务记录")
    public QuestRecord startQuest(
        @AuthenticationPrincipal Account account,
        @RequestParam("index") @ParamDoc("任务在全局记录中的索引") int index
    ) {
        return fxjlService.startQuest(account.getId(), index);
    }

    @PostMapping("/resetQuest")
    @WebInterfaceDoc(description = "重置一个福星降临任务", response = "")
    public WebMessageWrapper resetQuest(
        @AuthenticationPrincipal Account account,
        @RequestParam("index") @ParamDoc("任务在全局记录中的索引") int index
    ) {
        fxjlService.resetQuest(account.getId(), index);
        return WebMessageWrapper.ok();
    }

    @PostMapping("/obtainAward")
    @WebInterfaceDoc(description = "领取奖励", response = "奖励结果")
    public AwardResult obtainAward(@AuthenticationPrincipal Account account) {
        return fxjlService.obtainAward(account.getId());
    }
}
