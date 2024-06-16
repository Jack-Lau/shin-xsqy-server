/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.cultivation;

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
 * @author Administrator
 */
@RestController
@RequestMapping("/cultivation")
@ModuleDoc(moduleName = "修炼")
public class CultivationController {

    @Autowired
    CultivationService cultivationService;

    @RequestMapping("/get")
    @WebInterfaceDoc(description = "查看自己的修炼", response = "修炼信息")
    public CultivationRecord get(@AuthenticationPrincipal Account account) {
        return cultivationService.get(account.getId());
    }

    @PostMapping("/make")
    @WebInterfaceDoc(description = "修炼培养", response = "修炼信息")
    public CultivationRecord make(
            @AuthenticationPrincipal Account account,
            @RequestParam("cultivationIndex") @ParamDoc("修炼的序号") int cultivationIndex,
            @RequestParam("amountToConsume") @ParamDoc("要消耗的材料的数量") long amountToConsume
    ) {
        return cultivationService.make(account.getId(), cultivationIndex, amountToConsume);
    }

}
