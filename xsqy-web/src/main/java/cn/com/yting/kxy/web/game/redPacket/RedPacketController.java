/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.redPacket;

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
@RequestMapping("/redPacket")
@ModuleDoc(moduleName = "红包六六六")
public class RedPacketController {

    @Autowired
    RedPacketService redPacketService;

    @RequestMapping("/get")
    @WebInterfaceDoc(description = "查看全场记录", response = "聚合记录")
    public RedPacketOverall get(@AuthenticationPrincipal Account account) {
        return redPacketService.get(account.getId());
    }

    @RequestMapping("/today")
    @WebInterfaceDoc(description = "查看今日记录", response = "聚合记录")
    public List<RedPacket> today(@AuthenticationPrincipal Account account) {
        return redPacketService.today(account.getId());
    }

    @PostMapping("/take")
    @WebInterfaceDoc(description = "领取未领取的仙石", response = "聚合记录")
    public RedPacketOverall take(@AuthenticationPrincipal Account account) {
        return redPacketService.take(account.getId());
    }

    @PostMapping("/open")
    @WebInterfaceDoc(description = "抢一个红包", response = "聚合记录")
    public RedPacketOverall open(
            @AuthenticationPrincipal Account account,
            @RequestParam("redPacketId") @ParamDoc("红包的id") long redPacketId
    ) {
        return redPacketService.open(account.getId(), redPacketId);
    }

}
