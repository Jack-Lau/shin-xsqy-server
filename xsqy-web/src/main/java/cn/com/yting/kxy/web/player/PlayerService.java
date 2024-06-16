/*
 * Created 2018-6-26 16:09:49
 */
package cn.com.yting.kxy.web.player;

import java.util.Date;
import java.util.Map;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.parameter.AggregateParameterSpace;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.ParameterSpaceBuilder;
import cn.com.yting.kxy.core.parameter.resource.Attributes;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.wordfilter.ForbiddenWordsChecker;
import cn.com.yting.kxy.web.KxyWebConstants;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyChangedEvent;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.player.resource.PlayerLevelupExp;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class PlayerService implements ParameterSpaceProvider, InitializingBean {

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerNameUsedRepository playerNameUsedRepository;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ChatService chatService;

    @Autowired
    private ForbiddenWordsChecker forbiddenWordsChecker;
    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private TimeProvider timeProvider;

    private ParameterSpace playerBaseParameterSpace;

    @Override
    public void afterPropertiesSet() throws Exception {
        ParameterSpaceBuilder builder = ParameterSpace.builder();
        resourceContext.getLoader(Attributes.class).getAll().values().stream()
                .filter(it -> it.getBasicValueOfCharacter() != 0)
                .forEach(it -> builder.simple(it.getName(), it.getBasicValueOfCharacter()));
        builder.simple(ParameterNameConstants.战斗力, 100);
        playerBaseParameterSpace = builder.build();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Player createPlayer(long accountId, String playerName, int prefabId) {
        verifyPrefabId(prefabId);
        if (playerRepository.existsById(accountId)) {
            throw PlayerException.playerAlreadyCreated();
        }
        if (playerRepository.existsByPlayerName(playerName)) {
            throw PlayerException.playerNameExisted(playerName);
        }
        if (!KxyWebConstants.PLAYER_NAME_PATTERN.matcher(playerName).matches() || forbiddenWordsChecker.check(playerName)) {
            throw PlayerException.playerNameIllegal(playerName);
        }

        Player player = new Player();
        player.setAccountId(accountId);
        player.setPlayerName(playerName);
        player.setPrefabId(prefabId);
        long count = playerRepository.count();
        player.setGenesis(count < PlayerConstants.GENESIS_COUNT_LIMIT);
        player.setSerialNumber(count + 1);
        player.setCreateTime(new Date(timeProvider.currentTime()));
        player.setLastLoginTime(player.getCreateTime());
        player = playerRepository.saveAndFlush(player);

        return player;
    }

    public Player rename(long accountId, String playerName) {
        if (currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_改名卡).getAmount() < 1) {
            throw PlayerException.改名卡不足();
        }
        if (playerRepository.existsByPlayerName(playerName)) {
            throw PlayerException.playerNameExisted(playerName);
        }
        if (!KxyWebConstants.PLAYER_NAME_PATTERN.matcher(playerName).matches() || forbiddenWordsChecker.check(playerName)) {
            throw PlayerException.playerNameIllegal(playerName);
        }
        PlayerNameUsed newPnu = playerNameUsedRepository.findByUsedName(playerName).orElse(null);
        if (newPnu != null && newPnu.getAccountId() != accountId) {
            throw PlayerException.新名称是他人的曾用名();
        }
        //
        Player player = playerRepository.findByIdForWrite(accountId);
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_改名卡, 1, true);
        PlayerNameUsed prevPnu = playerNameUsedRepository.findByUsedName(player.getPlayerName()).orElse(null);
        if (prevPnu == null) {
            prevPnu = new PlayerNameUsed();
            prevPnu.setAccountId(accountId);
            prevPnu.setUsedName(player.getPlayerName());
            playerNameUsedRepository.saveAndFlush(prevPnu);
        }
        chatService.sendSystemMessage(ChatConstants.SERVICE_ID_UNDIFINED, ChatMessage.createTemplateMessage(
                3200047,
                ImmutableMap.of(
                        "playerName", player.getPlayerName(),
                        "newPlayerName", playerName)
        ));
        player.setPlayerName(playerName);
        //
        return player;
    }

    public void levelup(long accountId) {
        Player player = playerRepository.findByIdForWrite(accountId);
        int beforeLevel = player.getPlayerLevel();
        long exp = currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_经验).getAmount();
        long expToConsume = 0;
        boolean leveluped = false;
        Map<Long, PlayerLevelupExp> map = resourceContext.getLoader(PlayerLevelupExp.class).getAll();
        while (true) {
            PlayerLevelupExp playerLevelupExp = map.get(Long.valueOf(player.getPlayerLevel()));
            if (playerLevelupExp == null) {
                break;
            }
            if (exp - expToConsume < playerLevelupExp.getExp()) {
                break;
            }
            player.increasePlayerLevel();
            expToConsume += playerLevelupExp.getExp();
            leveluped = true;
        }
        if (leveluped) {
            currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_经验, expToConsume);
            eventPublisher.publishEvent(new PlayerLevelupEvent(this, accountId, beforeLevel, player.getPlayerLevel()));
        }
    }

    private void verifyPrefabId(long prefabId) {
        if (!(prefabId >= 4_000_001L && prefabId <= 4_000_004L)) {
            throw PlayerException.prefabIdIllegal(prefabId);
        }
    }

    @Override
    public ParameterSpace createParameterSpace(long accountId) {
        Player player = playerRepository.findById(accountId).orElse(null);
        if (player == null) {
            return ParameterSpace.EMPTY;
        }
        int level = player.getPlayerLevel();
        return new AggregateParameterSpace(
                playerBaseParameterSpace,
                ParameterSpace.builder()
                        .simple(ParameterNameConstants.最大生命, level * 12.8)
                        .simple(ParameterNameConstants.物伤, level * 2.5)
                        .simple(ParameterNameConstants.法伤, level * 2.5)
                        .simple(ParameterNameConstants.物防, level * 1.6)
                        .simple(ParameterNameConstants.法防, level * 1.6)
                        .simple(ParameterNameConstants.速度, level * 0.8)
                        .simple(ParameterNameConstants.幸运, level * 0.45)
                        .simple(ParameterNameConstants.战斗力, level * 15)
                        .build()
        );
    }

    @EventListener
    public void onCurrencyChangedEvent(CurrencyChangedEvent event) {
        if (event.getCurrencyId() == CurrencyConstants.ID_经验 && event.getAfterAmount() > event.getBeforeAmount()) {
            levelup(event.getAccountId());
        }
    }

}
