/*
 * Created 2018-11-16 15:21:24
 */
package cn.com.yting.kxy.web.auction;

import java.util.List;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/auction")
@ModuleDoc(moduleName = "auction")
public class AuctionController {

    @Autowired
    private CommodityRepository commodityRepository;

    @Autowired
    private AuctionService auctionService;

    @RequestMapping("/createRecord")
    @WebInterfaceDoc(description = "创建拍卖模块的玩家记录", response = "创建的记录")
    public AuctionRecord createRecord(@AuthenticationPrincipal Account account) {
        return auctionService.createRecord(account.getId());
    }

    @RequestMapping("/overall")
    @WebInterfaceDoc(description = "查询拍卖总览信息", response = "拍卖总览信息")
    public AuctionOverall overall(@AuthenticationPrincipal Account account) {
        return auctionService.getOverall(account.getId());
    }

    @PostMapping("/commodity/{id}/like")
    @WebInterfaceDoc(name = "like", description = "给指定拍卖品点赞", response = "")
    public WebMessageWrapper like(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("拍卖品 id") long commodityId
    ) {
        auctionService.like(account.getId(), commodityId);
        return WebMessageWrapper.ok();
    }

    @PostMapping("/commodity/{id}/bid")
    @WebInterfaceDoc(name = "bid", description = "对指定拍卖品出价", response = "状态变化后的拍卖品")
    public Commodity bid(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("拍卖品 id") long commodityId,
        @RequestParam("price") @ParamDoc("出价") long price
    ) {
        return auctionService.bid(account.getId(), commodityId, price);
    }

    @RequestMapping("/commodity/deliverable")
    @WebInterfaceDoc(name = "deliverableCommodities", description = "查询可以领取的成交的拍卖品", response = "对应的拍卖品集合")
    public List<Commodity> getDeliverableCommodities(
        @AuthenticationPrincipal Account account
    ) {
        return commodityRepository.findDeliverable(account.getId());
    }

    @PostMapping("/withdrawAll")
    @WebInterfaceDoc(description = "领取暂存的元宝和所有成交的拍卖品", response = "领取结果")
    public CommodityWithdrawResult withdrawAll(@AuthenticationPrincipal Account account) {
        return auctionService.withdrawAll(account.getId());
    }
}
