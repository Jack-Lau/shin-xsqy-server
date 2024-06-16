/*
 * Created 2019-1-24 11:47:44
 */
package cn.com.yting.kxy.web.legendarypet;

import java.util.List;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import cn.com.yting.kxy.web.pet.Pet;
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
@RequestMapping("/legendaryPet")
@ModuleDoc(moduleName = "神兽")
public class LegendaryPetController {

    @Autowired
    private LegendaryPetGenerationRepository legendaryPetGenerationRepository;

    @Autowired
    private LegendaryPetService legendaryPetService;

    @RequestMapping("/generation/")
    @WebInterfaceDoc(description = "查询所有神兽生成记录", response = "神兽生成记录")
    public List<LegendaryPetGenerationRecord> viewGenerations() {
        return legendaryPetGenerationRepository.findAll();
    }

    @PostMapping("/redeem")
    @WebInterfaceDoc(description = "神兽兑换", response = "宠物记录")
    public Pet redeem(
        @AuthenticationPrincipal Account account,
        @RequestParam("definitionId") @ParamDoc("要兑换的宠物定义的id") long definitionId
    ) {
        return legendaryPetService.redeem(account.getId(), definitionId);
    }

    @PostMapping("/ascend")
    @WebInterfaceDoc(description = "神兽进阶", response = "宠物记录")
    public Pet ascend(
        @AuthenticationPrincipal Account account,
        @RequestParam("petId") @ParamDoc("宠物id") long petId
    ) {
        return legendaryPetService.ascend(account.getId(), petId);
    }

    @PostMapping("/redeemSpecial")
    @WebInterfaceDoc(description = "神兽消耗品获得", response = "宠物记录")
    public Pet redeemSpecial(
        @AuthenticationPrincipal Account account,
        @RequestParam("currencyId") @ParamDoc("用于兑换的货币id") long currencyId
    ) {
        return legendaryPetService.redeemSpecial(account.getId(), currencyId);
    }
}
