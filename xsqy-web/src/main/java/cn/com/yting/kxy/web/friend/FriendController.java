/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.friend;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import cn.com.yting.kxy.web.message.WebsocketMessageService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Darkholme
 */
@RestController
@RequestMapping("/friend")
@ModuleDoc(moduleName = "friend")
public class FriendController {

    @Autowired
    FriendService friendService;
    @Autowired
    WebsocketMessageService websocketMessageService;

    @RequestMapping("/get")
    @WebInterfaceDoc(name = "get", description = "查询自己的好友列表", response = "好友列表")
    public List<Friend> get(@AuthenticationPrincipal Account account) {
        return friendService.get(account.getId());
    }

    @RequestMapping("/getApply")
    @WebInterfaceDoc(name = "getApply", description = "查询别人对自己发起的好友申请列表", response = "申请列表")
    public List<Friend> getApply(@AuthenticationPrincipal Account account) {
        return friendService.getApply(account.getId());
    }

    @RequestMapping("/recommend")
    @WebInterfaceDoc(name = "recommend", description = "查询推荐的好友列表", response = "推荐好友集合")
    public FriendRecommend recommend(@AuthenticationPrincipal Account account) {
        return friendService.recommend(account.getId());
    }

    @PostMapping("/find")
    @WebInterfaceDoc(name = "find", description = "查询指定的一个好友", response = "好友")
    public Friend find(
            @AuthenticationPrincipal Account account,
            @RequestParam("accountIdOrName") @ParamDoc("角色Id或角色名称") String accountIdOrName) {
        return friendService.find(accountIdOrName);
    }

    @PostMapping("/apply")
    @WebInterfaceDoc(name = "apply", description = "对某个好友发起好友申请", response = "是否发起成功")
    public Boolean apply(
            @AuthenticationPrincipal Account account,
            @RequestParam("targetId") @ParamDoc("目标角色Id") long targetId) {
        return friendService.apply(account.getId(), targetId);
    }

    @PostMapping("/handle")
    @WebInterfaceDoc(name = "handle", description = "处理别人对自己发起的好友申请", response = "是否通过")
    public Boolean handle(
            @AuthenticationPrincipal Account account,
            @RequestParam("actorId") @ParamDoc("发起好友申请的角色Id") long actorId,
            @RequestParam("pass") @ParamDoc("是否通过") boolean pass) {
        return friendService.handle(actorId, account.getId(), pass);
    }

    @PostMapping("/batchHandle")
    @WebInterfaceDoc(name = "batchHandle", description = "批量处理别人对自己发起的好友申请", response = "是否通过")
    public Boolean batchHandle(
            @AuthenticationPrincipal Account account,
            @RequestParam("pass") @ParamDoc("是否通过") boolean pass) {
        return friendService.batchHandle(account.getId(), pass);
    }

    @PostMapping("/delete")
    @WebInterfaceDoc(name = "delete", description = "从好友列表中删除一个指定的好友", response = "是否删除")
    public Boolean delete(
            @AuthenticationPrincipal Account account,
            @RequestParam("targetId") @ParamDoc("目标角色Id") long targetId) {
        return friendService.delete(account.getId(), targetId);
    }

    @TransactionalEventListener
    public void onApply(FriendApplyEvent event) {
        websocketMessageService.sendToUser(event.getTargetId(), "/friend/apply/receive", event.getActor());
    }

    @TransactionalEventListener
    public void onApplyPass(FriendApplyPassEvent event) {
        websocketMessageService.sendToUser(event.getActorId(), "/friend/apply/pass", event.getTarget());
    }

}
