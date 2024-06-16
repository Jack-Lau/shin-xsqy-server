/*
 * Created 2018-8-28 16:51:35
 */
package cn.com.yting.kxy.web.ethereum;

import java.util.ArrayList;
import java.util.List;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.message.WebsocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/ethereumExchange")
public class EthereumExchangeController implements ModuleApiProvider {

    @Autowired
    private WithdrawRequestRepository withdrawRequestRepository;
    @Autowired
    private DepositRequestRepository depositRequestRepository;

    @Autowired
    private EthereumExchangeService ethereumExchangeService;
    @Autowired
    private WebsocketMessageService websocketMessageService;

    @PostMapping("/withdrawKuaibi")
    public WithdrawRequest withdrawKuaibi(
        @AuthenticationPrincipal Account account,
        @RequestParam("amount") long amount,
        @RequestParam("toAddress") String toAddress
    ) {
        return ethereumExchangeService.withdrawKuaibi(account.getId(), amount, toAddress);
    }

    @PostMapping("/depositKuaibi")
    public DepositRequest depositKuaibi(
        @AuthenticationPrincipal Account account,
        @RequestParam("amount") long amount,
        @RequestParam("fromAddress") String fromAddress
    ) {
        return ethereumExchangeService.depositeKuaibi(account.getId(), amount, fromAddress);
    }

    @RequestMapping("/viewPendingWithDrawRequests")
    public List<WithdrawRequest> viewPendingWithDrawRequests(
        @AuthenticationPrincipal Account account
    ) {
        return withdrawRequestRepository.findByAccountIdAndRequestStatus(account.getId(), WithdrawRequestStatus.PENDING);
    }

    @RequestMapping("/viewPendingDepositRequests")
    public List<DepositRequest> viewPendingDepositRequests(
        @AuthenticationPrincipal Account account
    ) {
        return depositRequestRepository.findByAccountIdAndRequestStatus(account.getId(), DepositRequestStatus.PENDING);
    }

    @TransactionalEventListener
    public void onWithdrawCompleted(WithdrawCompletedEvent event) {
        WithdrawRequest request = event.getRequest();
        websocketMessageService.sendToUser(request.getAccountId(), "/ethereumExchange/withdrawCompleted", request);
    }

    @TransactionalEventListener
    public void onDepositCompleted(DepositCompletedEvent event) {
        DepositRequest request = event.getRequest();
        websocketMessageService.sendToUser(request.getAccountId(), "/ethereumExchange/depositCompleted", request);
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .name("ethereum exchange")
            .baseUri("/ethereumExchange")
            //
            .webInterface()
            .uri("/withdrawKuaibi")
            .post()
            .description("将块币提取到指定的钱包地址")
            .requestParameter("integer", "amount", "要提取的块币的数量（不包含手续费）")
            .requestParameter("string", "toAddress", "要提取到的钱包地址")
            .requestCaptchaParameters()
            .response(WithdrawRequest.class, "提现请求记录")
            .and()
            //
            .webInterface()
            .uri("/depositKuaibi")
            .post()
            .description("从指定的钱包地址存入块币")
            .requestParameter("integer", "amount", "要存入的块币的数量")
            .requestParameter("string", "fromAddress", "块币来源的钱包地址")
            .requestCaptchaParameters()
            .response(DepositRequest.class, "存入请求记录")
            .and()
            //
            .webInterface()
            .uri("/viewPendingDepositRequests")
            .description("查询自己的当前暂挂中的存入请求")
            .responseArray(DepositRequest.class, "存入请求的集合")
            .and()
            //
            .webInterface()
            .uri("/viewPendingWithDrawRequests")
            .description("查询自己的当前暂挂中的提现请求")
            .responseArray(WithdrawRequest.class, "提现请求的集合")
            .and()
            //
            //
            //
            .webNotification()
            .queue("/ethereumExchange/withdrawCompleted")
            .description("提现请求完成的通知")
            .messageType(WithdrawRequest.class)
            .and()
            //
            .webNotification()
            .queue("/ethereumExchange/depositCompleted")
            .description("存入请求完成的通知")
            .messageType(DepositRequest.class)
            .and();
    }
}
