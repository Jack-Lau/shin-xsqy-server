/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.baccarat;

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
@RequestMapping("/baccarat")
@ModuleDoc(moduleName = "baccarat")
public class BaccaratController {

    @Autowired
    BaccaratService baccaratService;

    @RequestMapping("/overall")
    @WebInterfaceDoc(name = "overall", description = "查询信息", response = "聚合信息")
    public BaccaratOverall overall(@AuthenticationPrincipal Account account) {
        return baccaratService.getOverall(account.getId());
    }

    @RequestMapping("/record")
    @WebInterfaceDoc(name = "record", description = "最近记录", response = "聚合信息")
    public List<BaccaratGame> record(@AuthenticationPrincipal Account account) {
        return baccaratService.getBaccaratGameRecords();
    }

    @PostMapping("/bet")
    @WebInterfaceDoc(name = "bet", description = "下注", response = "聚合信息")
    public BaccaratOverall bet(@AuthenticationPrincipal Account account,
            @RequestParam("betIndex") @ParamDoc("0庄赢 1闲赢 2和 3庄对 4闲对 5白板") int betIndex,
            @RequestParam("amount") @ParamDoc("毫块币") int amount) {
        return baccaratService.bet(account.getId(), betIndex, amount);
    }

    @PostMapping("/unBet")
    @WebInterfaceDoc(name = "unBet", description = "取消下注", response = "聚合信息")
    public BaccaratOverall unBet(@AuthenticationPrincipal Account account,
            @RequestParam("betIndex") @ParamDoc("0庄赢 1闲赢 2和 3庄对 4闲对 5白板") int betIndex) {
        return baccaratService.unBet(account.getId(), betIndex);
    }

}
