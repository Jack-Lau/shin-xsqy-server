/*
 * Created 2018-11-20 16:50:03
 */
package cn.com.yting.kxy.web.impartation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.DebugOnly;
import cn.com.yting.kxy.core.util.CommaSeparatedLists;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import cn.com.yting.kxy.web.controller.ControllerUtils;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/impartation")
@ModuleDoc(moduleName = "impartation")
public class ImpartationController {

    private static final Logger LOG = LoggerFactory.getLogger(ImpartationController.class);

    @Autowired
    private ImpartationRepository impartationRepository;
    @Autowired
    private DiscipleRepository discipleRepository;
    @Autowired
    private DisciplineRequestRepository disciplineRequestRepository;
    @Autowired
    private DailyPracticeRepository dailyPracticeRepository;

    @Autowired
    private ImpartationService impartationService;

    @RequestMapping("/view/myself")
    @WebInterfaceDoc(name = "viewMyself", description = "查询自己的师徒模块记录", response = "师徒模块记录")
    public ImpartationRecord viewMyself(@AuthenticationPrincipal Account account) {
        return impartationRepository.findById(account.getId()).orElseThrow(ControllerUtils::notFoundException);
    }

    @RequestMapping("/disciple/meAsDisciple")
    @WebInterfaceDoc(name = "viewMeAsDisciple", description = "查询自己作为徒弟的师徒关系记录", response = "师徒关系记录")
    public DiscipleRecord viewMeAsDisciple(@AuthenticationPrincipal Account account) {
        return discipleRepository.findById(account.getId()).orElseThrow(ControllerUtils::notFoundException);
    }

    @RequestMapping("/disciple/myDisciples")
    @WebInterfaceDoc(name = "viewMyDisciples", description = "查询自己作为师父的师徒关系记录", response = "师徒关系记录")
    public List<DiscipleRecord> viewMyDisciples(@AuthenticationPrincipal Account account) {
        return discipleRepository.findByMasterAccountId(account.getId());
    }

    @RequestMapping("/disciple/countByMasterAccountId")
    @WebInterfaceDoc(description = "查询指定玩家的未出师的徒弟数量", response = "数量")
    public int countDiscipleByMasterAccountId(
        @RequestParam("masterAccountId") @ParamDoc("要查询的玩家的账号 id") long masterAccountId
    ) {
        return discipleRepository.countByNotEndDisciples(masterAccountId);
    }

    @PostMapping("/createOrChangeRole")
    @WebInterfaceDoc(description = "创建记录或变更自己的师徒模块的角色", response = "师徒模块记录")
    public ImpartationRecord createOrChangeRole(
        @AuthenticationPrincipal Account account,
        @RequestParam("role") @ParamDoc("要变更的角色") ImpartationRole role
    ) {
        return impartationService.createOrChangeRole(account.getId(), role);
    }

    @RequestMapping("/disciplineRequest/fromMe/")
    @WebInterfaceDoc(description = "查询自己发起的师徒请求", response = "师徒请求")
    public List<DisciplineRequest> viewRequestsFromMe(@AuthenticationPrincipal Account account) {
        return disciplineRequestRepository.findByAccountId(account.getId());
    }

    @PostMapping("/disciplineRequest/fromMe/{masterAccountId}/create")
    @WebInterfaceDoc(description = "创建师徒请求", response = "创建的师徒请求")
    public DisciplineRequest createRequest(
        @AuthenticationPrincipal Account account,
        @PathVariable("masterAccountId") @ParamDoc("师父的账号 id") long masterAccountId
    ) {
        return impartationService.createDisciplineRequest(account.getId(), masterAccountId);
    }

    @RequestMapping("/disciplineRequest/toMe/")
    @WebInterfaceDoc(description = "查询发送给自己的师徒请求", response = "师徒请求")
    public List<DisciplineRequest> viewRequestsToMe(@AuthenticationPrincipal Account account) {
        return disciplineRequestRepository.findByMasterAccountId(account.getId());
    }

    @PostMapping("/disciplineRequest/toMe/{discipleAccountId}/accept")
    @WebInterfaceDoc(description = "接受一个师徒请求", response = "师徒关系记录")
    public DiscipleRecord acceptRequest(
        @AuthenticationPrincipal Account account,
        @PathVariable("discipleAccountId") @ParamDoc("徒弟的账号 id") long discipleAccountId
    ) {
        return impartationService.acceptDisciplineRequest(account.getId(), discipleAccountId);
    }

    @PostMapping("/disciplineRequest/toMe/clean")
    @WebInterfaceDoc(description = "清空发送给自己的师徒请求", response = "")
    public WebMessageWrapper cleanRequestsToMe(@AuthenticationPrincipal Account account) {
        disciplineRequestRepository.deleteInBulkByMasterAccountId(account.getId());
        return WebMessageWrapper.ok();
    }

    @RequestMapping("/dailyPractice/mine/")
    @WebInterfaceDoc(description = "查询自己的每日修行记录", response = "每日修行记录")
    public List<DailyPracticeRecord> viewDailyPractice(@AuthenticationPrincipal Account account) {
        return dailyPracticeRepository.findByAccountId(account.getId());
    }

    @RequestMapping("/dailyPractice/{accountId}/")
    @WebInterfaceDoc(description = "查询指定账号的每日修行记录", response = "每日修行记录")
    public List<DailyPracticeRecord> viewDailyPracticeByAccountId(
        @PathVariable("accountId") @ParamDoc("要查询的账号 id") long accountId
    ) {
        return dailyPracticeRepository.findByAccountId(accountId);
    }

    @PostMapping("/dailyPractice/mine/generate")
    @WebInterfaceDoc(description = "生成每日修行，如果已经生成过了则返回现有的", response = "进行中的每日修行记录")
    public List<DailyPracticeRecord> generateDailyPractice(@AuthenticationPrincipal Account account) {
        return impartationService.generateDailyPractice(account.getId());
    }

    @PostMapping("/dailyPractice/mine/{id}/complete")
    @WebInterfaceDoc(description = "完成指定的每日修行", response = "每日修行记录")
    public CompleteDailyPracticeResult completeDailyPractice(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("每日修行的 id") long definitionId
    ) {
        return impartationService.completeDailyPractice(account.getId(), definitionId);
    }

    @PostMapping("/dailyPractice/mine/complete")
    @WebInterfaceDoc(description = "完成指定的每日修行", response = "每日修行记录")
    public List<CompleteDailyPracticeResult> completeDailyPracticeInBatch(
        @AuthenticationPrincipal Account account,
        @RequestParam("ids") @ParamDoc("逗号分隔的每日修行 id 的列表") String ids
    ) {
        return CommaSeparatedLists.fromText(ids, Long::valueOf).stream()
            .map(id -> {
                try {
                    return impartationService.completeDailyPractice(account.getId(), id);
                } catch (Exception ex) {
//                    LOG.info("尝试完成成就时发生异常，id=" + id, ex);
                    return null;
                }
            })
            .collect(Collectors.toList());
    }

    @PostMapping("/dailyPractice/mine/{id}/obtainAward")
    @WebInterfaceDoc(description = "获取指定的每日修行的奖励", response = "获取奖励的结果")
    public ObtainDailyPracticeRewardResult obtainDailyPracticeReward(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("每日修行的 id") long definitionId
    ) {
        return impartationService.obtainDailyPracticeReward(account.getId(), definitionId);
    }

    @PostMapping("/disciple/meAsDisciple/confirm")
    @WebInterfaceDoc(description = "作为徒弟确认出师", response = "师徒关系记录")
    public DiscipleRecord confirmDisciplineEndAsDisciple(@AuthenticationPrincipal Account account) {
        return impartationService.confirmDisciplineEndAsDisciple(account.getId());
    }

    @PostMapping("/disciple/myDisciples/{id}/confirm")
    @WebInterfaceDoc(description = "作为师父确认出师", response = "师徒关系记录")
    public DiscipleRecord confirmDisciplineEndAsMaster(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("徒弟的账号 id") long discipleAccountId
    ) {
        return impartationService.confirmDisciplineEndAsMaster(account.getId(), discipleAccountId);
    }

    @PostMapping("/disciple/meAsDisciple/obtainHuoyuePoolAward")
    @WebInterfaceDoc(description = "作为徒弟领取师徒活跃点奖励", response = "获得的货币堆")
    public CurrencyStack obtainHuoyuePoolAwardAsDisciple(@AuthenticationPrincipal Account account) {
        return impartationService.obtainHuoyuePoolAwardAsDisciple(account.getId());
    }

    @PostMapping("/disciple/myDisciples/{id}/obtainHuoyuePoolAward")
    @WebInterfaceDoc(description = "作为师父领取师徒活跃点奖励", response = "获得的货币堆")
    public CurrencyStack obtainHuoyuePoolAwardAsMaster(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("徒弟的账号 id") long discipleAccountId
    ) {
        return impartationService.obtainHuoyuePoolAwardAsMaster(account.getId(), discipleAccountId);
    }

    @PostMapping("/disciple/meAsDisciple/obtainContributionExpPoolAward")
    @WebInterfaceDoc(description = "领取门贡经验奖励", response = "获得的货币堆")
    public List<CurrencyStack> obtainContributionExpPoolAward(@AuthenticationPrincipal Account account) {
        return impartationService.obtainContributionExpPoolAward(account.getId());
    }

    @RequestMapping("/viewRandomMasterAccountId")
    @WebInterfaceDoc(description = "查询一些随机的师父的账号 id", response = "账号 id")
    public List<CandidateMaster> viewRandomMasterAccountId() {
        List<CandidateMaster> list = impartationRepository.findAcceptableMasterAccountIds();
        Collections.shuffle(list);
        if (list.size() > 30) {
            list = list.subList(0, 30);
        }
        return list;
    }

    @DebugOnly
    @RequestMapping("/triggerDailyReset")
    @WebInterfaceDoc(description = "触发每日重置（测试用）", response = "")
    public void triggerDailyReset() {
        impartationService.dailyReset();
    }

    @RequestMapping("/disciple/{id}/currentHuoyuePool")
    @WebInterfaceDoc(description = "获得当前的活跃池的值", response = "获得的货币堆")
    public long calculateCurrentHuoyuePool(
        @PathVariable("id") @ParamDoc("要查询的徒弟的账号 id") long accountId
    ) {
        return impartationService.calculateCurrentHuoyuePool(accountId);
    }

    @PostMapping("/disciple/meAsDisciple/delete")
    public WebMessageWrapper deleteDiscipleRecordAsDisciple(
        @AuthenticationPrincipal Account account
    ) {
        impartationService.deleteDiscipleReocrdAsDisciple(account.getId());
        return WebMessageWrapper.ok();
    }

    @PostMapping("/disciple/myDisciples/{id}/delete")
    public WebMessageWrapper deleteDiscipleRecordAsMaster(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("徒弟的账号 id") long discipleAccountId
    ) {
        impartationService.deleteDiscipleReocrdAsMaster(account.getId(), discipleAccountId);
        return WebMessageWrapper.ok();
    }
}
