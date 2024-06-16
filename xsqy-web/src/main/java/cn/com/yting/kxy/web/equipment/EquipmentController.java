/*
 * Created 2018-9-18 11:47:57
 */
package cn.com.yting.kxy.web.equipment;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.DebugOnly;
import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.controller.ControllerUtils;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import cn.com.yting.kxy.web.price.PriceConstants;
import cn.com.yting.kxy.web.price.PriceService;
import cn.com.yting.kxy.web.recycling.RecyclingResult;
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
@RequestMapping("/equipment")
public class EquipmentController implements ModuleApiProvider {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private PriceService priceService;

    @Autowired
    private ResourceContext resourceContext;

    @RequestMapping("/view/{id}")
    public Equipment view(@PathVariable("id") long id) {
        return equipmentRepository.findById(id).orElseThrow(ControllerUtils::notFoundException);
    }

    @RequestMapping("/view/{id}/detail")
    public EquipmentDetail viewDetail(@PathVariable("id") long id) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(ControllerUtils::notFoundException);
        return equipment.toDetail(resourceContext);
    }

    @RequestMapping("/view/{id}/parameters")
    public List<Parameter> viewParameters(@PathVariable("id") long id) {
        Equipment equipment = equipmentRepository.findById(id).orElseThrow(ControllerUtils::notFoundException);
        return equipment.createParameterSpace(resourceContext).asRootParameterSpace().toParameters();
    }

    @RequestMapping("/viewMine")
    public List<Equipment> viewMyself(@AuthenticationPrincipal Account account) {
        return equipmentRepository.findByAccountId(account.getId());
    }

    @DebugOnly
    @PostMapping("/createForTest")
    public Equipment createForTest(
            @AuthenticationPrincipal Account account,
            @RequestParam("definitionId") long definitionId
    ) {
        return equipmentService.createForTest(account.getId(), definitionId);
    }

    @PostMapping("/action/{id}/arm")
    public WebMessageWrapper arm(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long id
    ) {
        equipmentService.arm(account.getId(), id);
        return WebMessageWrapper.ok();
    }

    @PostMapping("/action/{id}/enhance")
    public EnhancingResult enhance(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long id,
            @RequestParam(name = "useInsurance", defaultValue = "false") boolean useInsurance
    ) {
        return equipmentService.enhance(account.getId(), id, useInsurance);
    }

    @PostMapping("/action/{id}/fusion")
    public FusionResult fusion(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long equipmentId,
            @RequestParam("subEquipmentId") long subEquipmentId
    ) {
        return equipmentService.fusion(account.getId(), equipmentId, subEquipmentId);
    }

    @PostMapping("/action/{id}/soul")
    public Equipment soul(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long id
    ) {
        return equipmentService.soul(account.getId(), id);
    }

    @PostMapping("/action/{id}/wash")
    public Equipment wash(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long id
    ) {
        return equipmentService.wash(account.getId(), id);
    }

    @PostMapping("/disarm")
    public WebMessageWrapper disarm(
            @AuthenticationPrincipal Account account,
            @RequestParam("partType") int partType
    ) {
        equipmentService.disarm(account.getId(), partType);
        return WebMessageWrapper.ok();
    }

    @RequestMapping("/forgePrice")
    public long forgePrice() {
        return priceService.getCurrentPrice(PriceConstants.ID_装备打造);
    }

    @PostMapping("/forge")
    public Equipment forge(
            @AuthenticationPrincipal Account account,
            @RequestParam("expectedPrice") long expectedPrice
    ) {
        return equipmentService.forge(account.getId(), expectedPrice);
    }

    @PostMapping("/recycle")
    public List<RecyclingResult> recycle(
            @AuthenticationPrincipal Account account,
            @RequestParam("ids") String idsText
    ) {
        if (idsText.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = Arrays.stream(idsText.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        return equipmentService.recycle(account.getId(), ids);
    }

    @RequestMapping("/lastestInterestingForgings")
    public Collection<EquipmentForgingLog> lastestInterestingForgings() {
        return equipmentService.getLatestInterestingForgings();
    }

    @RequestMapping("/prototype/{id}")
    public EquipmentDetail viewPrototype(@PathVariable("id") long equipmentId) {
        return equipmentService.createEquipmentByPrototype(equipmentId).toDetail(resourceContext);
    }

    @PostMapping("/redeem")
    public Equipment redeem(
            @AuthenticationPrincipal Account account,
            @RequestParam("currencyId") long currencyId
    ) {
        return equipmentService.redeem(account.getId(), currencyId);
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
                .name("equipment")
                .baseUri("/equipment")
                //
                .webInterface()
                .name("view")
                .uri("/view/{id}")
                .description("查询一个装备装备")
                .requestParameter("integer", "id", "装备的id")
                .response(Equipment.class, "装备信息")
                .and()
                //
                .webInterface()
                .name("viewDetail")
                .uri("/view/{id}/detail")
                .description("查询一个装备的详细信息")
                .requestParameter("integer", "id", "装备的id")
                .response(EquipmentDetail.class, "装备详细信息")
                .and()
                //
                .webInterface()
                .name("viewParameters")
                .uri("/view/{id}/parameters")
                .description("查询一个装备的能力参数")
                .requestParameter("integer", "id", "装备的id")
                .responseArray(Parameter.class, "装备的能力参数")
                .and()
                //
                .webInterface()
                .uri("/viewMine")
                .description("查询自己的装备")
                .responseArray(Equipment.class, "装备的集合")
                .and()
                //
                .webInterface()
                .uri("/createForTest")
                .post()
                .description("生成一个装备（测试用）")
                .requestParameter("integer", "definitionId", "装备定义的id")
                .response(Equipment.class, "生成的装备")
                .and()
                //
                .webInterface()
                .name("arm")
                .uri("/action/{id}/arm")
                .post()
                .description("穿上一个装备")
                .requestParameter("integer", "id", "装备id")
                .and()
                //
                .webInterface()
                .name("enhance")
                .uri("/action/{id}/enhance")
                .post()
                .description("强化一个装备")
                .requestParameter("integer", "id", "装备id")
                .requestParameter("bool", "useInsurance", "是否使用强化保护卡")
                .response(EnhancingResult.class, "强化的结果")
                .and()
                //
                .webInterface()
                .name("fusion")
                .uri("/action/{id}/fusion")
                .post()
                .description("装备重铸")
                .requestParameter("number", "id", "需要重铸的装备id")
                .requestParameter("number", "subEquipmentId", "作为材料的装备id")
                .response(FusionResult.class, "重铸的结果")
                .and()
                //
                .webInterface()
                .name("soul")
                .uri("/action/{id}/soul")
                .post()
                .description("装备附魂-提升附魂等级")
                .requestParameter("number", "id", "需要提升附魂等级的装备id")
                .response(Equipment.class, "附魂结束的装备")
                .and()
                //
                .webInterface()
                .name("wash")
                .uri("/action/{id}/wash")
                .post()
                .description("装备附魂-洗炼附魂词条")
                .requestParameter("number", "id", "需要洗炼附魂词条的装备id")
                .response(Equipment.class, "附魂结束的装备")
                .and()
                //
                .webInterface()
                .uri("/disarm")
                .post()
                .description("卸下一个装备")
                .requestParameter("integer", "partType", "装备部位的编号")
                .and()
                //
                .webInterface()
                .uri("/forgePrice")
                .description("获得当前打造价格")
                .response("number", "当前价格")
                .and()
                //
                .webInterface()
                .uri("/forge")
                .post()
                .description("打造装备")
                .requestParameter("integer", "expectedPrice", "期望的价格")
                .response(Equipment.class, "生成的装备")
                .and()
                //
                .webInterface()
                .uri("/recycle")
                .post()
                .description("回收装备")
                .requestParameter("sring", "ids", "逗号分隔的装备 id 列表")
                .responseArray(RecyclingResult.class, "回收的结果列表")
                .and()
                //
                .webInterface()
                .uri("/lastestInterestingForgings")
                .description("获得最近的优质打造记录")
                .responseArray(EquipmentForgingLog.class, "打造记录")
                .and()
                //
                .webInterface()
                .uri("/prototype/{id}")
                .description("取得从一个原型能够生成的装备的视图")
                .requestParameter("number", "id", "神装的定义 id")
                .response(EquipmentDetail.class, "对应的装备")
                .and()
                //
                .webInterface()
                .uri("/redeem")
                .post()
                .description("兑换一件装备")
                .requestParameter("number", "currencyId", "用来兑换的货币 id")
                .response(Equipment.class, "兑换结果")
                .and();
    }

}
