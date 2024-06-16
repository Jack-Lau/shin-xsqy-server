/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.brawl;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Darkholme
 */
@RestController
@RequestMapping("/brawl")
@ModuleDoc(moduleName = "brawl")
public class BrawlController {

    @Autowired
    BrawlService brawlService;

    @RequestMapping("/get")
    @WebInterfaceDoc(name = "get", description = "查询自己的乱斗信息", response = "乱斗信息")
    public BrawlOverall get(@AuthenticationPrincipal Account account) {
        return brawlService.get(account.getId());
    }

    @PostMapping("/reset")
    @WebInterfaceDoc(name = "reset", description = "重置乱斗状态至等待组队", response = "乱斗信息")
    public BrawlOverall reset(@AuthenticationPrincipal Account account) {
        return brawlService.reset(account.getId());
    }

    @PostMapping("/team")
    @WebInterfaceDoc(name = "team", description = "随机生成乱斗队伍", response = "乱斗信息")
    public BrawlOverall team(@AuthenticationPrincipal Account account) {
        return brawlService.team(account.getId());
    }

    @PostMapping("/start")
    @WebInterfaceDoc(name = "start", description = "开始一场乱斗战斗", response = "乱斗信息")
    public BrawlOverall start(@AuthenticationPrincipal Account account) {
        return brawlService.start(account.getId());
    }

    @PostMapping("/finish")
    @WebInterfaceDoc(name = "finish", description = "结算一场乱斗战斗", response = "乱斗信息")
    public BrawlOverall finish(@AuthenticationPrincipal Account account) {
        return brawlService.finish(account.getId());
    }

    @PostMapping("/award")
    @WebInterfaceDoc(name = "award", description = "领奖", response = "乱斗信息")
    public BrawlOverall award(@AuthenticationPrincipal Account account) {
        return brawlService.award(account.getId());
    }

}
