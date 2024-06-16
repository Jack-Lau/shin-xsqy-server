/*
 * Created 2018-9-13 12:27:58
 */
package cn.com.yting.kxy.web.school;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.controller.ControllerUtils;
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
@RequestMapping("/school")
public class SchoolController implements ModuleApiProvider {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private SchoolService schoolService;

    @RequestMapping("/view/myself")
    public SchoolRecord viewMyself(@AuthenticationPrincipal Account account) {
        return schoolRepository.findById(account.getId()).orElseThrow(ControllerUtils::notFoundException);
    }

    @PostMapping("/create")
    public SchoolRecord create(
            @AuthenticationPrincipal Account account,
            @RequestParam("schoolId") long schoolId
    ) {
        return schoolService.create(account.getId(), schoolId);
    }

    @PostMapping("/levelup")
    public SchoolRecord levelup(
            @AuthenticationPrincipal Account account,
            @RequestParam("abilityIndex") int abilityIndex
    ) {
        return schoolService.levelup(account.getId(), abilityIndex);
    }

    @PostMapping("/levelupAMAP")
    public SchoolRecord levelupAMAP(
            @AuthenticationPrincipal Account account
    ) {
        return schoolService.levelupAMAP(account.getId());
    }

    @PostMapping("/redeemExtraAbilityLevelLimit")
    public SchoolRecord redeemExtraAbilityLevelLimit(
            @AuthenticationPrincipal Account account,
            @RequestParam("currencyId") long currencyId
    ) {
        return schoolService.redeemExtraAbilityLevelLimit(account.getId(), currencyId);
    }

    @PostMapping("/redeemChangeSchool")
    public SchoolRecord redeemChangeSchool(
            @AuthenticationPrincipal Account account,
            @RequestParam("currencyId") long currencyId,
            @RequestParam("schoolId") long schoolId
    ) {
        return schoolService.redeemChangeSchool(account.getId(), currencyId, schoolId);
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
                .name("school")
                .baseUri("/school")
                //
                .webInterface()
                .name("viewMyself")
                .uri("/view/myself")
                .description("查询自己的门派记录")
                .response(SchoolRecord.class, "门派记录")
                .and()
                //
                .webInterface()
                .uri("/create")
                .post()
                .description("创建门派记录（拜入门派）")
                .requestParameter("integer", "schoolId", "要加入的门派的 id")
                .response(SchoolRecord.class, "门派记录")
                .and()
                //
                .webInterface()
                .uri("/levelup")
                .post()
                .description("升级门派技能")
                .requestParameter("integer", "abilityIndex", "要升级的门派技能的索引")
                .response(SchoolRecord.class, "门派记录")
                .and()
                //
                .webInterface()
                .uri("/levelupAMAP")
                .post()
                .description("一键升级门派技能")
                .response(SchoolRecord.class, "门派记录")
                .and()
                //
                .webInterface()
                .uri("/redeemExtraAbilityLevelLimit")
                .post()
                .description("用货币兑换门派技能等级上限提升")
                .requestParameter("number", "currencyId", "要兑换的货币id")
                .response(SchoolRecord.class, "门派记录")
                .and()
                //
                .webInterface()
                .uri("/redeemChangeSchool")
                .post()
                .description("用货币转换门派")
                .requestParameter("number", "currencyId", "要兑换的货币id")
                .requestParameter("integer", "schoolId", "要加入的门派的 id")
                .response(SchoolRecord.class, "门派记录")
                .and();
    }
}
