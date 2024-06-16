/*
 * Created 2018-10-30 17:51:19
 */
package cn.com.yting.kxy.web.ranking;

import java.util.List;

import cn.com.yting.kxy.core.DebugOnly;
import cn.com.yting.kxy.core.resetting.ResetType;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/ranking")
public class RankingController implements ModuleApiProvider {

    @Autowired
    private RankingService rankingService;

    @RequestMapping("/view/{id}")
    public SimpleRanking viewRanking(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long rankingId,
            @RequestParam(name = "topRecordSize", defaultValue = "100") int topRecordSize
    ) {
        if (topRecordSize > 100) {
            topRecordSize = 100;
        }
        return rankingService.viewRanking(account.getId(), rankingId, topRecordSize);
    }

    @RequestMapping("/view/{rankingId}/{accountId}/")
    public List<SimpleRankingRecord> viewRankingRecords(
        @PathVariable("rankingId") long rankingId,
        @PathVariable("accountId") long accountId
    ) {
        return rankingService.viewRankingRecords(accountId, rankingId);
    }

    @DebugOnly
    @PostMapping("/triggerDailyReset")
    public void triggerDailyReset() {
        rankingService.anyReset(ResetType.DAILY);
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
                .name("ranking")
                .baseUri("/ranking")
                //
                .webInterface()
                .name("view")
                .uri("/view/{id}")
                .description("查询指定排行榜")
                .requestParameter("number", "id", "排行榜的 id")
                .requestParameter("number", "topRecordSize", "要查询的记录的长度（可选，默认100，最大100）")
                .response(SimpleRanking.class, "排行榜信息")
                .and()
                //
                .webInterface()
                .name("viewRecords")
                .uri("/view/{rankingId}/{accountId}/")
                .description("查询指定排行榜的指定账号的记录")
                .requestParameter("number", "rankingId", "排行榜的 id")
                .requestParameter("number", "accountId", "账号 id")
                .responseArray(SimpleRankingRecord.class, "排行记录")
                .and()
                //
                .webInterface()
                .uri("/triggerDailyReset")
                .post()
                .description("触发每日重置（测试用）")
                .and();
    }

}
