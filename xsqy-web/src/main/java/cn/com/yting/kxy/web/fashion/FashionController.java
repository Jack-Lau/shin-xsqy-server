/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.fashion;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Darkholme
 */
@RestController
@RequestMapping("/fashion")
@ModuleDoc(moduleName = "fashion")
public class FashionController {

    @Autowired
    FashionService fashionService;

    @RequestMapping("/getFashion")
    @WebInterfaceDoc(name = "getFashion", description = "查询一件指定的时装信息", response = "时装信息")
    public Fashion getFashion(@AuthenticationPrincipal Account account,
            @RequestParam("fashionId") @ParamDoc("时装实例Id") long fashionId) {
        return fashionService.getFashion(fashionId);
    }

    @RequestMapping("/getDye")
    @WebInterfaceDoc(name = "getDye", description = "查询一个指定的染色信息", response = "染色信息")
    public FashionDye getDye(@AuthenticationPrincipal Account account,
            @RequestParam("dyeId") @ParamDoc("染色方案Id") long dyeId) {
        return fashionService.getDye(dyeId);
    }

    @RequestMapping("/getByAccountId")
    @WebInterfaceDoc(name = "getByAccountId", description = "查询自己所有的时装信息", response = "时装信息")
    public List<Fashion> getByAccountId(@AuthenticationPrincipal Account account) {
        return fashionService.getByAccountId(account.getId());
    }

    @RequestMapping("/getDyeByAccountIdAndDefinitionId")
    @WebInterfaceDoc(name = "getDyeByAccountIdAndDefinitionId", description = "查询自己指定时装原型的所有染色信息", response = "染色信息")
    public List<FashionDye> getDyeByAccountIdAndDefinitionId(@AuthenticationPrincipal Account account,
            @RequestParam("definitionId") @ParamDoc("时装原型Id") long definitionId) {
        return fashionService.getDyeByAccountIdAndDefinitionId(account.getId(), definitionId);
    }

    @PostMapping("/redeem")
    @WebInterfaceDoc(name = "redeem", description = "消耗货币兑换时装", response = "时装信息")
    public Fashion redeem(@AuthenticationPrincipal Account account,
            @RequestParam("currencyId") @ParamDoc("货币Id") long currencyId) {
        return fashionService.redeem(account.getId(), currencyId);
    }

    @PostMapping("/putOn")
    @WebInterfaceDoc(name = "putOn", description = "穿戴时装", response = "OK")
    public WebMessageWrapper putOn(@AuthenticationPrincipal Account account,
            @RequestParam("fashionId") @ParamDoc("时装实例Id") long fashionId) {
        fashionService.putOn(account.getId(), fashionId);
        return WebMessageWrapper.ok();
    }

    @PostMapping("/putOff")
    @WebInterfaceDoc(name = "putOff", description = "脱下时装", response = "OK")
    public WebMessageWrapper putOff(@AuthenticationPrincipal Account account) {
        fashionService.putOff(account.getId());
        return WebMessageWrapper.ok();
    }

    @PostMapping(path = "/addDye/{fashionId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @WebInterfaceDoc(name = "addDye", description = "增加一个染色方案", response = "时装信息")
    public Fashion addDye(
            @AuthenticationPrincipal Account account,
            @PathVariable("fashionId") @ParamDoc("时装实例id") long fashionId,
            @RequestBody FashionDye fashionDye
    ) {
        return fashionService.addDye(account.getId(), fashionId, fashionDye);
    }

    @PostMapping("/chooseDye")
    @WebInterfaceDoc(name = "chooseDye", description = "更换指定染色方案", response = "时装信息")
    public Fashion chooseDye(@AuthenticationPrincipal Account account,
            @RequestParam("fashionId") @ParamDoc("时装实例Id") long fashionId,
            @RequestParam("dyeId") @ParamDoc("染色方案Id") long dyeId) {
        return fashionService.chooseDye(account.getId(), fashionId, dyeId);
    }

    @PostMapping("/putOffDye")
    @WebInterfaceDoc(name = "putOffDye", description = "卸下染色方案", response = "时装信息")
    public Fashion putOffDye(@AuthenticationPrincipal Account account,
            @RequestParam("fashionId") @ParamDoc("时装实例Id") long fashionId) {
        return fashionService.putOffDye(account.getId(), fashionId);
    }

}
