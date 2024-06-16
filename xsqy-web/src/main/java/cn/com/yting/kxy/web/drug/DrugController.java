/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.drug;

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
 * @author Administrator
 */
@RestController
@RequestMapping("/drug")
@ModuleDoc(moduleName = "药品")
public class DrugController {

    @Autowired
    DrugService drugService;

    @RequestMapping("/get")
    @WebInterfaceDoc(description = "查看正在生效的药品", response = "药品信息")
    public List<DrugRecord> get(@AuthenticationPrincipal Account account) {
        return drugService.get(account.getId());
    }

    @PostMapping("/take")
    @WebInterfaceDoc(description = "服用一个药品", response = "服用结果")
    public DrugTakeResult take(
            @AuthenticationPrincipal Account account,
            @RequestParam("currencyId") @ParamDoc("药品的货币Id") long currencyId
    ) {
        return drugService.take(account.getId(), currencyId);
    }

}
