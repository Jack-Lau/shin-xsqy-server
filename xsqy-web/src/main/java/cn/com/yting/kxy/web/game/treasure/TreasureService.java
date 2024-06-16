/*
 * Created 2018-11-2 15:27:08
 */
package cn.com.yting.kxy.web.game.treasure;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.awardpool.AwardPoolConstants;
import cn.com.yting.kxy.web.awardpool.AwardPoolService;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiUnits;
import cn.com.yting.kxy.web.currency.resource.Currency;
import cn.com.yting.kxy.web.game.treasure.resource.TreasureAward;
import cn.com.yting.kxy.web.game.treasure.resource.TreasureAwardLoader;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class TreasureService implements InitializingBean {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private AwardPoolService awardPoolService;
    @Autowired
    private ChatService chatService;

    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void afterPropertiesSet() throws Exception {
        awardPoolService.registerPoolSelector(AwardPoolConstants.POOL_ID_TREASURE, () -> {
            return resourceContext.getByLoaderType(TreasureAwardLoader.class).getPoolSelector();
        });
    }

    public CurrencyStack obtainTreasure(long accountId) {
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_藏宝图, 1);
        TreasureAward award = (TreasureAward) awardPoolService.select(AwardPoolConstants.POOL_ID_TREASURE, accountId).iterator().next().getPayload();
        CurrencyStack result = new CurrencyStack(award.getCurrencyId(), award.getAmount());
        currencyService.increaseCurrency(accountId, result.getCurrencyId(), result.getAmount(), CurrencyConstants.PURPOSE_INCREMENT_宝图);
        long displayAmount = result.getAmount();
        if (result.getCurrencyId() == CurrencyConstants.ID_毫仙石) {
            displayAmount = KuaibiUnits.toKuaibi(displayAmount);
        }
        if (award.getBroadcastId() != 0) {
            chatService.sendSystemMessage(
                ChatConstants.SERVICE_ID_UNDIFINED,
                ChatMessage.createTemplateMessage(
                    award.getBroadcastId(),
                    ImmutableMap.of(
                        "playerName", playerRepository.findById(accountId).get().getPlayerName(),
                        "currency", resourceContext.getLoader(Currency.class).get(result.getCurrencyId()).getName(),
                        "amount", displayAmount
                    )
                )
            );
        }
        if (award.getInterfaceBroadcastId() != 0) {
            chatService.offerInterestingMessage(
                award.getInterfaceBroadcastId(),
                ChatMessage.createTemplateMessage(
                    award.getBroadcastId(),
                    ImmutableMap.of(
                        "playerName", playerRepository.findById(accountId).get().getPlayerName(),
                        "currency", resourceContext.getLoader(Currency.class).get(result.getCurrencyId()).getName(),
                        "amount", displayAmount
                    )
                )
            );
        }

        eventPublisher.publishEvent(new TreasureObtainedEvent(this, accountId));

        return result;
    }
}
