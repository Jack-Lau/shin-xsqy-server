/*
 * Created 2018-7-11 13:16:45
 */
package cn.com.yting.kxy.web.player;

import cn.com.yting.kxy.core.TimeProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.RootParameterSpace;
import cn.com.yting.kxy.core.parameter.resource.Attributes;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.equipment.Equipment;
import cn.com.yting.kxy.web.equipment.EquipmentRepository;
import cn.com.yting.kxy.web.fashion.Fashion;
import cn.com.yting.kxy.web.fashion.FashionDye;
import cn.com.yting.kxy.web.fashion.FashionDyeRepository;
import cn.com.yting.kxy.web.fashion.FashionRepository;
import cn.com.yting.kxy.web.invitation.InvitationRepository;
import cn.com.yting.kxy.web.invitation.InvitationService;
import cn.com.yting.kxy.web.player.PlayerOnlineStatus.Status;
import cn.com.yting.kxy.web.ranking.RankingService;
import cn.com.yting.kxy.web.ranking.SimpleRankingRecord;
import cn.com.yting.kxy.web.school.SchoolRecord;
import cn.com.yting.kxy.web.school.SchoolRepository;
import cn.com.yting.kxy.web.title.Title;
import cn.com.yting.kxy.web.title.TitleRepository;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import java.util.Date;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class CompositePlayerService implements InitializingBean {

    private final Cache<Long, PlayerOnlineStatus> onlinePlayers = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerRelationRepository playerRelationRepository;
    @Autowired
    private PlayerLocationRepository playerLocationRepository;
    @Autowired
    private TitleRepository titleRepository;
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private FashionRepository fashionRepository;
    @Autowired
    private FashionDyeRepository fashionDyeRepository;

    @Autowired
    private PlayerService playerService;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private RankingService rankingService;

    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private ParameterSpaceProviderBus parameterSpaceProviderBus;
    @Autowired
    private TimeProvider timeProvider;

    private List<String> exposableParameterNames;

    @Override
    public void afterPropertiesSet() throws Exception {
        exposableParameterNames = resourceContext.getLoader(Attributes.class).getAll().values().stream()
                .filter(Attributes::isExposable)
                .map(Attributes::getName)
                .collect(Collectors.toList());
        if (!exposableParameterNames.contains(ParameterNameConstants.战斗力)) {
            exposableParameterNames.add(ParameterNameConstants.战斗力);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Player createPlayerWithInvitation(long accountId, String playerName, int prefabId, String invitationCode) {
        invitationService.createInviterRecord(accountId, invitationCode, true);
        Player player = playerService.createPlayer(accountId, playerName, prefabId);
        if (player.isGenesis()) {
            invitationService.increaseInvitationLimitForGenesis(accountId);
        }
        String inviterPlayerName = invitationRepository.findByAccountId(accountId).stream()
                .filter(it -> it.isDirectInvitation())
                .findAny()
                .map(it -> playerRepository.findById(it.getInviterId())
                .map(p -> p.getPlayerName())
                .orElse(null))
                .orElse("角色名不存在");
        if (invitationService.isInvited(accountId)) {
            chatService.sendSystemMessage(ChatConstants.SERVICE_ID_UNDIFINED, ChatMessage.createTemplateMessage(
                    PlayerConstants.TEMPLATE_ID_IVITED_PLAYER_CREATED,
                    ImmutableMap.of(
                            "playerName", playerName,
                            "inviterName", inviterPlayerName
                    )
            ));
        }
        return player;
    }

    public PlayerBaseInfo getPlayerBaseInfo(long accountId) {
        Player player = playerRepository.findById(accountId).orElse(null);
        if (player == null) {
            return null;
        }
        Long schoolId = schoolRepository.findById(accountId)
                .map(SchoolRecord::getSchoolId)
                .orElse(null);
        Optional<PlayerRelation> optionalPlayerRelation = playerRelationRepository.findById(accountId);
        Long weaponId = optionalPlayerRelation.map(it -> it.getHandEquipmentId())
                .flatMap(it -> equipmentRepository.findById(it))
                .map(it -> it.getDefinitionId())
                .orElse(null);
        Long titleDefinitionId = optionalPlayerRelation.map(it -> it.getTitleId())
                .flatMap(it -> titleRepository.findById(it))
                .map(it -> it.getDefinitionId())
                .orElse(null);
        Fashion fashion = optionalPlayerRelation.map(it -> it.getFashionId())
                .flatMap(it -> fashionRepository.findById(it))
                .orElse(null);
        Long fashionDefinitionId = null;
        FashionDye fashionDye = null;
        if (fashion != null) {
            fashionDefinitionId = fashion.getDefinitionId();
            if (fashion.getDyeId() != 0) {
                fashionDye = fashionDyeRepository.findById(fashion.getDyeId()).orElse(null);
                if (fashionDye == null || fashionDye.getAccountId() != accountId) {
                    fashionDye = null;
                }
            }
        }
        boolean shenxing = optionalPlayerRelation.map(it -> it.getFootEquipmentId())
                .flatMap(it -> equipmentRepository.findById(it))
                .map(it -> it.exportEffectIds().contains(603L))
                .orElse(false);
        return new PlayerBaseInfo(player, schoolId, weaponId, titleDefinitionId, fashionDefinitionId, fashionDye, shenxing);
    }

    public Player getPlayer(long accountId) {
        return playerRepository.findById(accountId).orElse(null);
    }

    public int getPlayerLevel(long accountId) {
        Player player = playerRepository.findById(accountId).orElse(null);
        if (player == null) {
            return 0;
        } else {
            return player.getPlayerLevel();
        }
    }

    @Transactional(readOnly = true)
    public PlayerDetail getPlayerDetail(long accountId) {
        Player player = playerRepository.findById(accountId).orElse(null);
        if (player == null) {
            return null;
        }
        PlayerRelation playerRelation = playerRelationRepository.findOrDummy(accountId);
        Title title = Optional.ofNullable(playerRelation.getTitleId())
                .map(titleId -> titleRepository.findById(titleId).orElse(null))
                .orElse(null);
        List<Equipment> equipments = playerRelation.toEquipmentIds().stream()
                .filter(Objects::nonNull)
                .map(id -> equipmentRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        RootParameterSpace rootSpace = parameterSpaceProviderBus.createRootSpace(accountId);
        List<Parameter> parameters = exposableParameterNames.stream()
                .map(it -> rootSpace.getParameter(it))
                .collect(Collectors.toList());
        Long schoolId = schoolRepository.findById(accountId)
                .map(SchoolRecord::getSchoolId)
                .orElse(null);
        Fashion fashion = Optional.ofNullable(playerRelation.getFashionId())
                .map(fashionId -> fashionRepository.findById(fashionId).orElse(null))
                .orElse(null);
        FashionDye fashionDye = null;
        if (fashion != null && fashion.getDyeId() != 0) {
            fashionDye = fashionDyeRepository.findById(fashion.getDyeId()).orElse(null);
            if (fashionDye == null || fashionDye.getAccountId() != accountId) {
                fashionDye = null;
            }
        }
        return new PlayerDetail(player, title, fashion, fashionDye, schoolId, playerRelation, equipments, parameters);
    }

    public Player updateFc(long accountId) {
        Player player = playerRepository.findByIdForWrite(accountId);
        player.setFc((long) parameterSpaceProviderBus.createRootSpace(accountId).getParameter(ParameterNameConstants.战斗力).getValue());
        return player;
    }

    public PlayerOnlineStatus getOnlineStatus(long accountId) {
        PlayerOnlineStatus pos = onlinePlayers.getIfPresent(accountId);
        if (pos == null) {
            pos = new PlayerOnlineStatus(findOrCreatePlayerLocation(accountId), Status.OFFLINE);
        }
        return pos;
    }

    public boolean refreshOnlineStatus(long accountId, PlayerOnlineStatus playerOnlineStatus) {
        PlayerOnlineStatus pos = onlinePlayers.getIfPresent(accountId);
        PlayerLocation pl = findOrCreatePlayerLocation(accountId);
        if (pos == null) {
            pos = new PlayerOnlineStatus(pl, Status.OFFLINE);
        }
        Player player = playerRepository.findByIdForWrite(accountId);
        player.updateLastOnlineTime(new Date(timeProvider.currentTime()));
        if (pos.getStatus() == Status.OFFLINE) {
            pos.setStatus(Status.IDLE);
            if (player != null) {
                // 记录登录时间
                player.setLastLoginTime(new Date(timeProvider.currentTime()));
                // 角色登录广播
                trySendOnlineBroadcast(player);
                //
                playerRepository.save(player);
            }
        }
        //
        pl.setMapId(playerOnlineStatus.getPlayerLocation().getMapId());
        pl.setXPos(playerOnlineStatus.getPlayerLocation().getXPos());
        pl.setYPos(playerOnlineStatus.getPlayerLocation().getYPos());
        pl.setDirection(playerOnlineStatus.getPlayerLocation().getDirection());
        playerLocationRepository.save(pl);
        //
        pos.setPlayerLocation(pl);
        pos.setStatus(playerOnlineStatus.getStatus());
        onlinePlayers.put(accountId, pos);
        return true;
    }

    private void trySendOnlineBroadcast(Player player) {
        long accountId = player.getAccountId();
        long broadcastId = 0;
        if (accountId == PlayerConstants.KXY_CARP_ACCOUNT_ID) {
            broadcastId = PlayerConstants.KXY_CARP_BROADCAST_ID;
        }
        List<SimpleRankingRecord> topRecords;
        if (!(topRecords = rankingService.viewRanking(accountId, 4430001, 1).getTopRecords()).isEmpty() && topRecords.get(0).getAccountId() == accountId) {
            broadcastId = 3200038;
        } else if (!(topRecords = rankingService.viewRanking(accountId, 4430002, 1).getTopRecords()).isEmpty() && topRecords.get(0).getAccountId() == accountId) {
            broadcastId = 3200039;
        } else if (!(topRecords = rankingService.viewRanking(accountId, 4430003, 1).getTopRecords()).isEmpty() && topRecords.get(0).getAccountId() == accountId) {
            broadcastId = 3200040;
        } else if (!(topRecords = rankingService.viewRanking(accountId, 4430004, 1).getTopRecords()).isEmpty() && topRecords.get(0).getAccountId() == accountId) {
            broadcastId = 3200041;
        } else if (!(topRecords = rankingService.viewRanking(accountId, 4430005, 1).getTopRecords()).isEmpty() && topRecords.get(0).getAccountId() == accountId) {
            broadcastId = 3200042;
        } else if (!(topRecords = rankingService.viewRanking(accountId, 4430006, 1).getTopRecords()).isEmpty() && topRecords.get(0).getAccountId() == accountId) {
            broadcastId = 3200043;
        } else if (!(topRecords = rankingService.viewRanking(accountId, 4430008, 1).getTopRecords()).isEmpty() && topRecords.get(0).getAccountId() == accountId) {
            broadcastId = 3200044;
        }
        if (broadcastId != 0) {
            chatService.sendSystemMessage(ChatConstants.SERVICE_ID_UNDIFINED, ChatMessage.createTemplateMessage(
                    broadcastId,
                    ImmutableMap.of(
                            "playerName", player.getPlayerName()
                    )
            ));
        }
    }

    public List<PlayerOnlineStatus> getOnlineStatusByIdList(List<Long> accountIds) {
        List<PlayerOnlineStatus> result = new ArrayList<>();
        onlinePlayers
                .asMap()
                .values()
                .stream()
                .filter(
                        (pos) -> (pos.getStatus() != Status.OFFLINE && accountIds.contains(pos.getPlayerLocation().getAccountId())))
                .forEachOrdered((pos) -> {
                    result.add(pos);
                });
        return result;
    }

    public List<PlayerOnlineStatus> getOnlineStatusByMapId(long accountId, int mapId, int amount, List<Long> excludeIds) {
        List<PlayerOnlineStatus> result = new ArrayList<>();
        for (PlayerOnlineStatus pos : onlinePlayers.asMap().values()) {
            if (result.size() >= amount) {
                break;
            }
            if (pos.getStatus() != Status.OFFLINE
                    && pos.getPlayerLocation().getMapId() == mapId
                    && pos.getPlayerLocation().getAccountId() != accountId
                    && !excludeIds.contains(pos.getPlayerLocation().getAccountId())) {
                result.add(pos);
            }
        }
        return result;
    }

    public boolean isThisPlayerOnline(long accountId) {
        PlayerOnlineStatus pos = onlinePlayers.getIfPresent(accountId);
        return !(pos == null || pos.getStatus() == Status.OFFLINE);
    }

    public List<Long> getAllOnlinePlayerIds() {
        List<Long> result = new ArrayList<>();
        onlinePlayers.asMap().values().stream().filter((pos) -> (pos.getStatus() != Status.OFFLINE)).forEachOrdered((pos) -> {
            result.add(pos.getPlayerLocation().getAccountId());
        });
        return result;
    }

    public List<Long> findOnlineRandomPlayerIdsExcludeSelf(long id, int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit 必须为正数：" + limit);
        }
        List<Long> rawIds = getAllOnlinePlayerIds();
        List<Long> ids = new ArrayList<>();
        rawIds.stream().filter((playerId) -> (playerId != id)).forEachOrdered((playerId) -> {
            ids.add(playerId);
        });
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        if (limit > ids.size()) {
            limit = ids.size();
        }
        Random random = RandomProvider.getRandom();
        for (int i = 0; i < limit; i++) {
            int randomNumber = random.nextInt(ids.size() - i);
            if (randomNumber == 0) {
                continue;
            }
            int targetIndex = i + randomNumber;
            Collections.swap(ids, i, targetIndex);
        }
        return new ArrayList<>(ids.subList(0, limit));
    }

    public List<Player> findPlayersExcludeMyTeamByFC(
            long startFC,
            long endFC,
            long accountId,
            long accountId_su1,
            long accountId_su2,
            int number,
            double fluctuation_low,
            double fluctuation_high) {
        List<Player> playerList;
        int playerNumber = playerRepository.countPlayersExcludeMyTeamByFC(startFC, endFC, accountId, accountId_su1, accountId_su2);
        if (playerNumber >= 10) {
            playerList = playerRepository.findPlayersExcludeMyTeamByFC(startFC, endFC, accountId, accountId_su1, accountId_su2, number);
            return playerList;
        } else {
            return findPlayersExcludeMyTeamByFC(
                    (long) Math.floor(startFC * fluctuation_low),
                    (long) Math.floor(endFC * fluctuation_high),
                    accountId,
                    accountId_su1,
                    accountId_su2,
                    number,
                    fluctuation_low,
                    fluctuation_high);
        }
    }

    private PlayerLocation findOrCreatePlayerLocation(long accountId) {
        PlayerLocation playerLocation = playerLocationRepository.findByAccountId(accountId);
        if (playerLocation == null) {
            playerLocation = new PlayerLocation();
            playerLocation.setAccountId(accountId);
            playerLocation.setMapId(PlayerConstants.DEFAULT_MAP_ID);
            playerLocation.setXPos(PlayerConstants.DEFAULT_X_POS);
            playerLocation.setYPos(PlayerConstants.DEFAULT_Y_POS);
            playerLocation.setDirection(PlayerConstants.DEFAULT_DIRECTION);
            playerLocation = playerLocationRepository.save(playerLocation);
        }
        return playerLocation;
    }

}
