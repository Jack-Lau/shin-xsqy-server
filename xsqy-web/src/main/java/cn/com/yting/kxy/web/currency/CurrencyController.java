/*
 * Created 2018-6-27 15:48:59
 */
package cn.com.yting.kxy.web.currency;

import java.util.List;

import cn.com.yting.kxy.core.DebugOnly;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiDailyRecord;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiService;
import cn.com.yting.kxy.web.message.WebsocketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/currency")
public class CurrencyController implements ModuleApiProvider {

    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private CurrencyChangeLogRepository currencyChangeLogRepository;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private WebsocketMessageService messageService;
    @Autowired
    private KuaibiService kuaibiService;

    @RequestMapping("/view/{accountId}")
    public List<CurrencyRecord> viewByAccountId(
        @PathVariable("accountId") long accountId
    ) {
        return currencyRepository.findByAccountId(accountId);
    }

    @RequestMapping("/view/{accountId}/{currencyId}")
    public CurrencyRecord viewByAccountIdAndCurrencyId(
        @PathVariable("accountId") long accountId,
        @PathVariable("currencyId") long currencyId
    ) {
        return currencyService.findOrDummyRecord(accountId, currencyId);
    }

    @RequestMapping("/logs")
    @DebugOnly
    public Object viewLogs() {
        return currencyChangeLogRepository.findAll();
    }

    @PostMapping("/giveMeMoney")
    @DebugOnly
    public Object giveMeMoney(
        @AuthenticationPrincipal Account account,
        @RequestParam("currencyId") long currencyId,
        @RequestParam("amount") long amount
    ) {
        currencyService.increaseCurrency(account.getId(), currencyId, amount);
        return new RedirectView(String.format("view/%s/%d", account.getId(), currencyId));
    }

    @RequestMapping("/lastDayKuaibiDailiRecord")
    public KuaibiDailyRecord lastDayKuaibiDailiRecord() {
        return kuaibiService.getLastDayKuaibiDailyRecord();
    }

    @RequestMapping("/todayKuaibiDailyRecord")
    public KuaibiDailyRecord todayKuaibiDailyRecord() {
        return kuaibiService.getTodayKuaibiDailyRecord();
    }

    @TransactionalEventListener
    public void onCurrencyChanged(CurrencyChangedEvent event) {
        messageService.sendToUser(event.getAccountId(), "/currency/currencyChanged", event);
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .baseUri("/currency")
            .name("currency")
            //
            .webInterface()
            .name("viewAll")
            .uri("/view/{accountId}")
            .description("查询一个账号的所有货币信息")
            .requestParameter("integer", "accountId", "账号的id")
            .responseArray(CurrencyRecord.class, "对应的账号的货币信息的集合")
            .and()
            //
            .webInterface()
            .name("view")
            .uri("/view/{accountId}/{currencyId}")
            .description("查询一个账号的指定的货币的信息")
            .requestParameter("integer", "accountId", "账号的id")
            .requestParameter("integer", "currencyId", "货币的id")
            .response(CurrencyRecord.class, "对应的货币信息")
            .and()
            //
            .webInterface()
            .uri("/logs")
            .description("查询货币变化记录，调试用")
            .and()
            //
            .webInterface()
            .uri("/giveMeMoney")
            .post()
            .description("给当前登录的账号的增加货币，调试用")
            .requestParameter("integer", "currencyId", "货币id")
            .requestParameter("integer", "amount", "数量")
            .and()
            //
            .webInterface()
            .uri("/lastDayKuaibiDailiRecord")
            .description("获取前一天的块币日常记录")
            .response(KuaibiDailyRecord.class, "块币日常记录")
            .and()
            //
            .webInterface()
            .uri("/todayKuaibiDailyRecord")
            .description("获取今天的块币日常记录")
            .response(KuaibiDailyRecord.class, "块币日常记录")
            .and()
            //
            //
            //
            .webNotification()
            .queue("/currency/currencyChanged")
            .description("货币变化时的通知消息")
            .messageType(CurrencyChangedEvent.class)
            .and();
    }
}
