/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.changlefang;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import java.util.List;
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
@RequestMapping("/changlefang")
@ModuleDoc(moduleName = "changlefang")
public class ChanglefangController {

    @Autowired
    ChanglefangService changlefangService;

    @RequestMapping("/get")
    @WebInterfaceDoc(name = "get", description = "查询长乐坊信息（不包含记录）", response = "聚合信息")
    public ChanglefangOverall get(@AuthenticationPrincipal Account account) {
        return changlefangService.get(account.getId());
    }

    @RequestMapping("/log")
    @WebInterfaceDoc(name = "log", description = "查询长乐坊记录", response = "log")
    public List<ChanglefangLog> log(@AuthenticationPrincipal Account account) {
        return changlefangService.log(account.getId());
    }

    @PostMapping("/buy")
    @WebInterfaceDoc(name = "buy", description = "购买本票", response = "聚合信息")
    public ChanglefangOverall buy(@AuthenticationPrincipal Account account,
            @RequestParam("amount") @ParamDoc("购买的本票数量") int amount) {
        return changlefangService.buy(account.getId(), amount);
    }

    @PostMapping("/exchange_kc")
    @WebInterfaceDoc(name = "exchange_kc", description = "兑换块币", response = "聚合信息")
    public ChanglefangOverall exchange_kc(@AuthenticationPrincipal Account account,
            @RequestParam("amount") @ParamDoc("兑换的块币数量（不是毫块币）") int amount) {
        return changlefangService.exchange_kc(account.getId(), amount);
    }

}
