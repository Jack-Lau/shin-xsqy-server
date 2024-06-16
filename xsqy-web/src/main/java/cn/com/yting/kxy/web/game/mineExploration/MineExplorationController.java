/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.mineExploration;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Darkholme
 */
@RestController
@RequestMapping("/mineExploration")
@ModuleDoc(moduleName = "mineExploration")
public class MineExplorationController {

    @Autowired
    MineExplorationService mineExplorationService;

    @RequestMapping("/get")
    @WebInterfaceDoc(name = "get", description = "查询自己的挖矿信息", response = "聚合信息")
    public MineExplorationOverall get(@AuthenticationPrincipal Account account) {
        return mineExplorationService.get(account.getId());
    }

    @PostMapping("/start")
    @WebInterfaceDoc(name = "start", description = "开始一局挖矿", response = "聚合信息")
    public MineExplorationOverall start(@AuthenticationPrincipal Account account) {
        return mineExplorationService.start(account.getId());
    }

    @PostMapping("/dig")
    @WebInterfaceDoc(name = "dig", description = "挖开某个点", response = "聚合信息")
    public MineExplorationOverall dig(@AuthenticationPrincipal Account account,
            @RequestParam("row") @ParamDoc("行编号，0~4") int row,
            @RequestParam("column") @ParamDoc("列编号，0~4") int column) {
        return mineExplorationService.dig(account.getId(), row, column);
    }

    @PostMapping("/add")
    @WebInterfaceDoc(name = "add", description = "续命", response = "聚合信息")
    public MineExplorationOverall add(@AuthenticationPrincipal Account account) {
        return mineExplorationService.add(account.getId());
    }

    @PostMapping("/award")
    @WebInterfaceDoc(name = "award", description = "领奖", response = "聚合信息，这里的coupons是这次领奖发给他人的代金券，不是自己的")
    public MineExplorationOverall award(@AuthenticationPrincipal Account account) {
        return mineExplorationService.award(account.getId());
    }

    @PostMapping("/coupon")
    @WebInterfaceDoc(name = "coupon", description = "领代金券", response = "聚合信息")
    public MineExplorationOverall coupon(@AuthenticationPrincipal Account account,
            @RequestParam("couponSendId") @ParamDoc("代金券Id") long couponSendId) {
        return mineExplorationService.coupon(account.getId(), couponSendId);
    }

}
