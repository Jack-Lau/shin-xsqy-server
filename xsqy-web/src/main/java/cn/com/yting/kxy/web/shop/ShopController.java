/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.shop;

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
@RequestMapping("/shop")
@ModuleDoc(moduleName = "shop")
public class ShopController {

    @Autowired
    ShopService shopService;

    @PostMapping("/getCommodity")
    @WebInterfaceDoc(name = "getCommodity", description = "查询一个指定的商品", response = "商品记录")
    public ShopCommodityRecord getCommodity(@AuthenticationPrincipal Account account,
            @RequestParam("commodityId") @ParamDoc("商品Id") long commodityId) {
        return shopService.getCommodity(commodityId);
    }

    @PostMapping("/getShop")
    @WebInterfaceDoc(name = "getShop", description = "查询一个指定的商店", response = "商品记录List")
    public List<ShopCommodityRecord> getShop(@AuthenticationPrincipal Account account,
            @RequestParam("shopId") @ParamDoc("商店Id") long shopId) {
        return shopService.getShop(shopId);
    }

    @PostMapping("/buy")
    @WebInterfaceDoc(name = "buy", description = "购买商品", response = "商品记录List")
    public List<ShopCommodityRecord> buy(@AuthenticationPrincipal Account account,
            @RequestParam("shopId") @ParamDoc("商店Id") long shopId,
            @RequestParam("commodityId") @ParamDoc("商品Id") long commodityId,
            @RequestParam("amount") @ParamDoc("数量") long amount,
            @RequestParam("expectedPrice") @ParamDoc("单价") long expectedPrice) {
        return shopService.buy(account.getId(), shopId, commodityId, amount, expectedPrice);
    }

}
