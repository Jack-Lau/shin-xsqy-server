/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.slots;

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
@RequestMapping("/slots")
@ModuleDoc(moduleName = "slots")
public class SlotsController {

    @Autowired
    SlotsService slotsService;

    @RequestMapping("/get")
    @WebInterfaceDoc(name = "get", description = "查询自己的摇奖信息", response = "聚合信息")
    public SlotsOverall get(@AuthenticationPrincipal Account account) {
        return slotsService.get(account.getId());
    }

    @RequestMapping("/getLike")
    @WebInterfaceDoc(name = "getLike", description = "查询自己的被点赞信息", response = "被点赞信息")
    public List<SlotsLike> getLike(@AuthenticationPrincipal Account account) {
        return slotsService.getLike(account.getId());
    }

    @RequestMapping("/getFriendBigPrize")
    @WebInterfaceDoc(name = "getFriendBigPrize", description = "查询自己好友的大奖信息", response = "大奖信息")
    public List<SlotsBigPrize> getFriendBigPrize(@AuthenticationPrincipal Account account) {
        return slotsService.getFriendBigPrize(account.getId());
    }

    @PostMapping("/lock")
    @WebInterfaceDoc(name = "lock", description = "锁定/解锁坑位", response = "聚合信息")
    public SlotsOverall lock(@AuthenticationPrincipal Account account,
            @RequestParam("slotIndex") @ParamDoc("坑位编号0~3") int slotIndex,
            @RequestParam("lock") @ParamDoc("是否锁定") int lock) {
        return slotsService.lock(account.getId(), slotIndex, lock);
    }

    @PostMapping("/pull")
    @WebInterfaceDoc(name = "pull", description = "摇奖", response = "聚合信息")
    public SlotsOverall pull(@AuthenticationPrincipal Account account) {
        return slotsService.pull(account.getId());
    }

    @PostMapping("/take")
    @WebInterfaceDoc(name = "take", description = "领奖", response = "聚合信息")
    public SlotsOverall take(@AuthenticationPrincipal Account account) {
        return slotsService.take(account.getId());
    }

    @PostMapping("/like")
    @WebInterfaceDoc(name = "like", description = "点赞", response = "聚合信息")
    public SlotsOverall like(@AuthenticationPrincipal Account account,
            @RequestParam("bigPrizeId") @ParamDoc("大奖记录Id") long bigPrizeId) {
        return slotsService.like(account.getId(), bigPrizeId);
    }

    @PostMapping("/takeLike")
    @WebInterfaceDoc(name = "takeLike", description = "领被点赞奖", response = "聚合信息")
    public SlotsOverall takeLike(@AuthenticationPrincipal Account account) {
        return slotsService.takeLike(account.getId());
    }

}
