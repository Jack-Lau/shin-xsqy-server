/*
 * Created 2018-6-29 16:10:08
 */
package cn.com.yting.kxy.web.player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.controller.ControllerUtils;
import cn.com.yting.kxy.web.invitation.InvitationException;
import cn.com.yting.kxy.web.message.WebsocketMessageService;
import cn.com.yting.kxy.web.player.PlayerLocation.DIRECTION;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.event.TransactionalEventListener;
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
@RequestMapping("/player")
public class PlayerController implements ModuleApiProvider {

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerNameUsedRepository playerNameUsedRepository;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private CompositePlayerService compositePlayerService;
    @Autowired
    private WebsocketMessageService websocketMessageService;

    @RequestMapping("/view/myself")
    public Player viewMyself(@AuthenticationPrincipal Account account) {
        return viewById(account.getId());
    }

    @RequestMapping("/view/{id}")
    public Player viewById(@PathVariable("id") long id) {
        return playerRepository.findById(id)
                .orElseThrow(ControllerUtils::notFoundException);
    }

    @RequestMapping("/view/myself/detail")
    public PlayerDetail viewMyselfDetail(@AuthenticationPrincipal Account account) {
        return viewDetailById(account.getId());
    }

    @RequestMapping("/viewNameUsed/{id}")
    public List<String> viewNameUsedById(@PathVariable("id") long id) {
        List<String> result = new ArrayList<>();
        List<PlayerNameUsed> pnus = playerNameUsedRepository.findByAccountId(id);
        if (pnus != null) {
            pnus.forEach((pnu) -> {
                result.add(pnu.getUsedName());
            });
        }
        return result;
    }

    @PostMapping("/action/myself/rename")
    public Player rename(
            @AuthenticationPrincipal Account account,
            @RequestParam("playerName") String playerName
    ) {
        return playerService.rename(account.getId(), playerName);
    }

    @RequestMapping("/view/{id}/detail")
    public PlayerDetail viewDetailById(@PathVariable("id") long id) {
        return compositePlayerService.getPlayerDetail(id);
    }

    @RequestMapping("/view")
    public List<Player> view(
            @RequestParam("accountIds") String accountIds
    ) {
        return mapAccountIdsTo(accountIds, id -> playerRepository.findById(id).orElse(null));
    }

    @RequestMapping("/viewName")
    public List<String> viewName(
            @RequestParam("accountIds") String accountIds
    ) {
        return mapAccountIdsTo(accountIds, id -> playerRepository.findById(id).map(Player::getPlayerName).orElse(null));
    }

    @RequestMapping("/viewBaseInfo")
    public List<PlayerBaseInfo> viewBaseInfo(
            @RequestParam("accountIds") String accountIds
    ) {
        return mapAccountIdsTo(accountIds, id -> compositePlayerService.getPlayerBaseInfo(id));
    }

    @RequestMapping("/viewBaseInfoByName")
    public PlayerBaseInfo viewBaseInfoByName(
            @RequestParam("playerName") String playerName
    ) {
        Player player = playerRepository.findByPlayerName(playerName);
        if (player == null) {
            throw ControllerUtils.notFoundException();
        }
        return compositePlayerService.getPlayerBaseInfo(player.getAccountId());
    }

    @RequestMapping("/viewDetail")
    public List<PlayerDetail> viewDetail(
            @RequestParam("accountIds") String accountIds
    ) {
        return mapAccountIdsTo(accountIds, id -> compositePlayerService.getPlayerDetail(id));
    }

    private <T> List<T> mapAccountIdsTo(String accountIds, Function<Long, T> mapper) {
        if (accountIds.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(accountIds.split(","))
                .map(Long::parseLong)
                .map(mapper)
                .collect(Collectors.toList());
    }

    private List<Long> mapAccountIdsTo(String accountIds) {
        if (accountIds.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(accountIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    @RequestMapping("/ghosts")
    public List<Long> getGhosts(
            @AuthenticationPrincipal Account account,
            @RequestParam(name = "limit", required = false) Integer limit
    ) {
        return compositePlayerService.findOnlineRandomPlayerIdsExcludeSelf(account.getId(), limit);
    }

    @PostMapping("create")
    public Player createPlayer(
            @AuthenticationPrincipal Account account,
            @RequestParam("playerName") String playerName,
            @RequestParam("prefabId") int prefabId
    ) {
        return playerService.createPlayer(account.getId(), playerName, prefabId);
    }

    @PostMapping("createWithInvitation")
    public Player createPlayerWithInvitation(
            @AuthenticationPrincipal Account account,
            @RequestParam("playerName") String playerName,
            @RequestParam("prefabId") int prefabId,
            @RequestParam(name = "invitationCode", required = false) String invitationCode
    ) {
        return compositePlayerService.createPlayerWithInvitation(account.getId(), playerName, prefabId, invitationCode);
    }

    @RequestMapping("/count")
    public long countPlayers() {
        return playerRepository.count();
    }

    @PostMapping("/updateFc")
    public long updateFc(@AuthenticationPrincipal Account account) {
        return compositePlayerService.updateFc(account.getId()).getFc();
    }

    @RequestMapping("/getOnlineStatus")
    public PlayerOnlineStatus getOnlineStatus(@AuthenticationPrincipal Account account) {
        return compositePlayerService.getOnlineStatus(account.getId());
    }

    @PostMapping("/refreshOnlineStatus")
    public boolean refreshOnlineStatus(
            @AuthenticationPrincipal Account account,
            @RequestParam("mapId") int mapId,
            @RequestParam("xPos") int xPos,
            @RequestParam("yPos") int yPos,
            @RequestParam("direction") DIRECTION direction,
            @RequestParam("status") int status
    ) {
        PlayerLocation pl = new PlayerLocation();
        pl.setAccountId(account.getId());
        pl.setMapId(mapId);
        pl.setXPos(xPos);
        pl.setYPos(yPos);
        pl.setDirection(direction);
        PlayerOnlineStatus pos = new PlayerOnlineStatus(pl, PlayerOnlineStatus.getStatus(status));
        return compositePlayerService.refreshOnlineStatus(account.getId(), pos);
    }

    @PostMapping("/getOnlineStatusByIdList")
    public List<PlayerOnlineStatus> getOnlineStatusByIdList(
            @AuthenticationPrincipal Account account,
            @RequestParam("accountIds") String accountIds
    ) {
        return compositePlayerService.getOnlineStatusByIdList(mapAccountIdsTo(accountIds));
    }

    @PostMapping("/getOnlineStatusByMapId")
    public List<PlayerOnlineStatus> getOnlineStatusByMapId(
            @AuthenticationPrincipal Account account,
            @RequestParam("mapId") int mapId,
            @RequestParam("amount") int amount,
            @RequestParam("excludeIds") String excludeIds
    ) {
        return compositePlayerService.getOnlineStatusByMapId(account.getId(), mapId, amount, mapAccountIdsTo(excludeIds));
    }

    @RequestMapping("/isThisPlayerOnline/{id}")
    public boolean isThisPlayerOnline(@PathVariable("id") long id) {
        return compositePlayerService.isThisPlayerOnline(id);
    }

    @TransactionalEventListener
    public void onPlayerLevelup(PlayerLevelupEvent event) {
        websocketMessageService.sendToUser(event.getAccountId(), "/player/levelup", event);
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
                .baseUri("/player")
                .name("player")
                //
                .webInterface()
                .name("viewMyself")
                .uri("/view/myself")
                .description("查询自己的角色的信息")
                .response(Player.class, "对应的角色信息")
                .and()
                //
                .webInterface()
                .name("viewById")
                .uri("/view/{id}")
                .description("查询一个角色的信息")
                .requestParameter("integer", "id", "角色的id")
                .response(Player.class, "对应的角色信息")
                .and()
                //
                .webInterface()
                .name("viewMyselfDetail")
                .uri("/view/myself/detail")
                .description("查询自己的角色的详细信息")
                .response(PlayerDetail.class, "对应的角色详细信息")
                .and()
                //
                .webInterface()
                .name("viewNameUsedById")
                .uri("/viewNameUsed/{id}")
                .description("查询一个角色的曾用名")
                .requestParameter("integer", "id", "角色的id")
                .responseArray(String.class, "角色的曾用名")
                .and()
                //
                .webInterface()
                .name("rename")
                .uri("/action/myself/rename")
                .post()
                .description("角色改名")
                .requestParameter("string", "playerName", "角色名")
                .response(Player.class, "更改后的角色信息")
                .and()
                //
                .webInterface()
                .name("viewDetail")
                .uri("/view/{id}/detail")
                .description("查询一个角色的详细信息")
                .requestParameter("integer", "id", "角色的id")
                .response(PlayerDetail.class, "对应的角色详细信息")
                .and()
                //
                .webInterface()
                .uri("/view")
                .description("查询一些玩家的角色信息")
                .requestParameter("string", "accountIds", "逗号分隔的账号id列表")
                .responseArray(Player.class, "对应的角色信息")
                .and()
                //
                .webInterface()
                .uri("/viewName")
                .description("查询一些玩家的角色名")
                .requestParameter("string", "accountIds", "逗号分隔的账号id列表")
                .responseArray(String.class, "对应的角色名")
                .and()
                //
                .webInterface()
                .uri("/viewBaseInfo")
                .description("查询一些玩家的基本角色信息")
                .requestParameter("string", "accountIds", "逗号分隔的账号id列表")
                .responseArray(PlayerBaseInfo.class, "对应的角色基本信息")
                .and()
                //
                .webInterface()
                .uri("/viewBaseInfoByName")
                .description("用角色名查询一个玩家")
                .requestParameter("string", "playerName", "角色名")
                .response(PlayerBaseInfo.class, "对应的角色基本信息")
                .and()
                //
                .webInterface()
                .uri("/viewDetail")
                .description("查询一些玩家的详细角色信息")
                .requestParameter("string", "accountIds", "逗号分隔的账号id列表")
                .responseArray(PlayerDetail.class, "对应的角色详细信息")
                .and()
                //
                .webInterface()
                .uri("/ghosts")
                .description("查询一些可以当作玩家镜像的账号的id")
                .requestParameter("integer", "limit", "查询的数量限制")
                .responseArray(Long.class, "账号id")
                .and()
                //
                .webInterface()
                .uri("/create")
                .description("创建角色（保留作测试用）")
                .post()
                .requestParameter("string", "playerName", "角色名")
                .requestParameter("integer", "prefabId", "角色的造型id")
                .response(Player.class, "成功创建的角色的信息")
                .expectableError(PlayerException.EC_PLAYER_ALREADY_CREATED, "此账号已经创建过角色")
                .expectableError(PlayerException.EC_PLAYERNAME_EXISTED, "指定的角色名已经存在")
                .expectableError(PlayerException.EC_PLAYERNAME_ILLEGAL, "指定的角色名非法")
                .expectableError(PlayerException.EC_PREFABID_ILLEGAL, "指定的造型id非法")
                .and()
                //
                .webInterface()
                .uri("/createWithInvitation")
                .description("创建角色，并创建邀请关系")
                .post()
                .requestParameter("string", "playerName", "角色名")
                .requestParameter("integer", "prefabId", "角色的造型id")
                .requestParameter("string", "invitationCode", "上游邀请者的邀请码（可选）")
                .response(Player.class, "成功创建的角色的信息")
                .expectableError(PlayerException.EC_PLAYER_ALREADY_CREATED, "此账号已经创建过角色")
                .expectableError(PlayerException.EC_PLAYERNAME_EXISTED, "指定的角色名已经存在")
                .expectableError(PlayerException.EC_PLAYERNAME_ILLEGAL, "指定的角色名非法")
                .expectableError(InvitationException.EC_INVITER_RECORD_EXISTED, "邀请者记录已存在")
                .and()
                //
                .webInterface()
                .uri("/count")
                .description("获得当前的角色总数")
                .response("integer", "数量")
                .and()
                //
                .webInterface()
                .uri("/updateFc")
                .post()
                .description("更新并获得当前角色的战斗力")
                .response("integer", "战斗力")
                .and()
                //
                .webInterface()
                .uri("/getOnlineStatus")
                .description("获取自己的在线状态")
                .response("PlayerOnlineStatus", "角色的在线记录")
                .and()
                //
                .webInterface()
                .uri("/refreshOnlineStatus")
                .description("刷新自己的在线状态")
                .post()
                .requestParameter("int", "mapId", "地图id")
                .requestParameter("int", "xPos", "x坐标")
                .requestParameter("int", "yPos", "y坐标")
                .requestParameter("DIRECTION", "direction", "面向")
                .requestParameter("int", "status", "0:OFFLINE 1:IDLE 2:BATTLE 3:MINIGAME")
                .response(Boolean.class, "成功刷新则为true")
                .and()
                //
                .webInterface()
                .uri("/getOnlineStatusByIdList")
                .description("获取一堆角色的在线状态")
                .post()
                .requestParameter("string", "accountIds", "逗号分隔的账号id列表")
                .responseArray(PlayerOnlineStatus.class, "角色的在线记录")
                .and()
                //
                .webInterface()
                .uri("/getOnlineStatusByMapId")
                .description("获取指定地图角色的在线状态")
                .post()
                .requestParameter("int", "mapId", "地图id")
                .requestParameter("int", "amount", "角色数量")
                .requestParameter("string", "excludeIds", "需要过滤的角色，逗号分隔的账号id列表")
                .responseArray(PlayerOnlineStatus.class, "角色的在线记录")
                .and()
                //
                .webInterface()
                .uri("/isThisPlayerOnline/{id}")
                .description("查询该角色是否在线")
                .requestParameter("integer", "id", "角色的id")
                .response(Boolean.class, "该角色在线则为true，离线则为false")
                .and()
                //
                //
                //
                .webNotification()
                .queue("/player/levelup")
                .description("角色等级提升时的通知")
                .messageType(PlayerLevelupEvent.class)
                .and();
    }
}
