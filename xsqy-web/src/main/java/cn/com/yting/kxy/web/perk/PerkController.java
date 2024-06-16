/*
 * Created 2019-1-8 13:01:37
 */
package cn.com.yting.kxy.web.perk;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
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
@RequestMapping("/perk")
@ModuleDoc(moduleName = "天赋")
public class PerkController {

    @Autowired
    private PerkRingRepository perkRingRepository;

    @Autowired
    private PerkService perkService;

    @RequestMapping("/ring/myself")
    @WebInterfaceDoc(description = "查看自己的天赋环的信息", response = "天赋环信息")
    public PerkRingDetail viewMyself(@AuthenticationPrincipal Account account) {
        PerkRing perkRing = perkRingRepository.findById(account.getId()).orElseThrow(ControllerUtils::notFoundException);
        return perkService.perkRingToDetail(perkRing);
    }

    @PostMapping("/ring/create")
    @WebInterfaceDoc(description = "创建天赋环记录", response = "天赋环信息")
    public PerkRingDetail createRing(@AuthenticationPrincipal Account account) {
        PerkRing perkRing = perkService.createPerkRing(account.getId());
        return perkService.perkRingToDetail(perkRing);
    }

    @PostMapping("/ring/myself/makeProgress")
    @WebInterfaceDoc(description = "天赋培养", response = "天赋环信息")
    public PerkRingDetail makeProgress(
        @AuthenticationPrincipal Account account,
        @RequestParam("amountToConsume") @ParamDoc("要消耗的材料的数量") long amountToConsume
    ) {
        PerkRing perkRing = perkService.makeProgress(account.getId(), amountToConsume);
        return perkService.perkRingToDetail(perkRing);
    }

    @PostMapping("/ring/myself/makeSelection")
    @WebInterfaceDoc(description = "激活效果", response = "天赋环信息")
    public PerkRingDetail makePerkSelection(
        @AuthenticationPrincipal Account account,
        @RequestParam("index") @ParamDoc("天赋索引，从 0 开始") int index,
        @RequestParam("selection") @ParamDoc("天赋效果选择") PerkSelection selection
    ) {
        PerkRing perkRing = perkService.makePerkSelection(account.getId(), index, selection);
        return perkService.perkRingToDetail(perkRing);
    }

    @PostMapping("/ring/myself/switchSelection")
    @WebInterfaceDoc(description = "切换效果", response = "天赋环信息")
    public PerkRingDetail switchPerkSelection(
        @AuthenticationPrincipal Account account,
        @RequestParam("index") @ParamDoc("天赋索引，从 0 开始") int index,
        @RequestParam("selection") @ParamDoc("天赋效果选择") PerkSelection selection
    ) {
        PerkRing perkRing = perkService.switchPerkSelection(account.getId(), index, selection);
        return perkService.perkRingToDetail(perkRing);
    }
}
