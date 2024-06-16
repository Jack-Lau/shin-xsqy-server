/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.brawl;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.UnitBuilder;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.battle.BattleRepository;
import cn.com.yting.kxy.web.battle.BattleResponse;
import cn.com.yting.kxy.web.battle.BattleService;
import cn.com.yting.kxy.web.battle.BattleSession;
import cn.com.yting.kxy.web.battle.BattleUnitExporter;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.game.brawl.resource.BrawlStageInfo;
import cn.com.yting.kxy.web.player.CompositePlayerService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerBaseInfo;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Darkholme
 */
@Service
@Transactional
public class BrawlService implements InitializingBean, ResetTask {

    @Autowired
    CompositePlayerService compositePlayerService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    ChatService chatService;
    @Autowired
    BattleService battleService;

    @Autowired
    BrawlRepository brawlRepository;
    @Autowired
    BattleRepository battleRepository;
    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    ResourceContext resourceContext;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    BattleUnitExporter battleUnitExporter;

    Map<Long, List<UnitBuilder<?>>> teamUnitBuilders = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        teamUnitBuilders.clear();
    }

    public BrawlOverall get(long accountId) {
        return new BrawlOverall(
                findOrCreate(accountId),
                getTeamMembers(accountId));
    }

    public BrawlOverall reset(long accountId) {
        checkPlayerLevel(accountId);
        BrawlRecord br = findOrCreate(accountId);
        if (br.getStatus() != BrawlStatus.NOT_START) {
            throw BrawlException.brawlStatusIncorrect();
        }
        if (br.getResetCount() < 1) {
            throw BrawlException.insufficientResetCount();
        }
        teamUnitBuilders.put(accountId, new ArrayList<>());
        br.setResetCount(br.getResetCount() - 1);
        br.setBrawlCount(BrawlConstants.MAX_BRAWL_COUNT);
        br.setCurrentStage(0);
        br.setCurrentBattleSessionId(0);
        br.setStatus(BrawlStatus.CREATE_TEAM);
        br.setTeamMember_1(0);
        br.setTeamMember_2(0);
        br = brawlRepository.save(br);
        return new BrawlOverall(
                br,
                getTeamMembers(accountId));
    }

    public BrawlOverall team(long accountId) {
        checkPlayerLevel(accountId);
        BrawlRecord br = findOrCreate(accountId);
        if (br.getStatus() != BrawlStatus.CREATE_TEAM) {
            throw BrawlException.brawlStatusIncorrect();
        }
        long selfFc = compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getFc();
        br.setTeamMaxFc(selfFc);
        List<Player> teamMemberList = compositePlayerService.findPlayersExcludeMyTeamByFC(
                (long) Math.floor(selfFc * 0.8),
                (long) Math.floor(selfFc * 1.2),
                accountId,
                0,
                0,
                2,
                0.95,
                1.05);
        //
        List<UnitBuilder<?>> tmList = teamUnitBuilders.get(accountId);
        if (tmList == null) {
            tmList = new ArrayList<>();
        } else {
            tmList.clear();
        }
        for (Player p : teamMemberList) {
            tmList.add(createTeamMemberInternal(p.getAccountId()));
            if (p.getFc() > br.getTeamMaxFc()) {
                br.setTeamMaxFc(p.getFc());
            }
        }
        teamUnitBuilders.put(accountId, tmList);
        //
        br.setTeamMember_1(teamMemberList.get(0).getAccountId());
        br.setTeamMember_2(teamMemberList.get(1).getAccountId());
        br = brawlRepository.save(br);
        //
        return new BrawlOverall(
                br,
                getTeamMembers(accountId));
    }

    public BrawlOverall start(long accountId) {
        checkPlayerLevel(accountId);
        BrawlRecord br = findOrCreate(accountId);
        if (br.getStatus() == BrawlStatus.NOT_START || br.getStatus() == BrawlStatus.END) {
            throw BrawlException.brawlStatusIncorrect();
        }
        if (br.getCurrentStage() + 1 >= resourceContext.getLoader(BrawlStageInfo.class).getAll().size()) {
            throw BrawlException.brawlStatusIncorrect();
        }
        if (br.getBrawlCount() < 1) {
            throw BrawlException.insufficientBrawlCount();
        }
        //
        if (br.getStatus() == BrawlStatus.CREATE_TEAM) {
            br.setCurrentBattleSessionId(0);
            br.setStatus(BrawlStatus.IN_CHALLENGE);
        }
        //
        List<UnitBuilder<?>> teamUnits = teamUnitBuilders.get(accountId);
        if (teamUnits == null || teamUnits.size() < 1) {
            teamUnits = new ArrayList<>();
            if (br.getTeamMember_1() != 0) {
                teamUnits.add(createTeamMemberInternal(br.getTeamMember_1()));
            }
            if (br.getTeamMember_2() != 0) {
                teamUnits.add(createTeamMemberInternal(br.getTeamMember_2()));
            }
            teamUnitBuilders.put(accountId, teamUnits);
        }
        //
        BrawlStageInfo bsi = resourceContext.getLoader(BrawlStageInfo.class).get(br.getCurrentStage());
        long selfFc = compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getFc();
        long teamMaxFc = br.getTeamMaxFc() == null ? 0 : br.getTeamMaxFc();
        long maxFc = Math.max(selfFc, teamMaxFc);
        if (maxFc < 20000 && br.getCurrentStage() > 1) {
            maxFc += 20000;
        }
        List<Player> enemyList = compositePlayerService.findPlayersExcludeMyTeamByFC(
                (long) Math.floor(bsi.getMinConstantOne() + maxFc * bsi.getMinConstantTwo()),
                (long) Math.floor(bsi.getMaxConstantOne() + maxFc * bsi.getMaxConstantTwo()),
                accountId,
                br.getTeamMember_1(),
                br.getTeamMember_2(),
                3,
                0.95,
                1.05);
        List<Long> enemyAccountIdList = new ArrayList<>();
        enemyList.forEach((enemy) -> {
            enemyAccountIdList.add(enemy.getAccountId());
        });
        //
        BattleSession battleSession = battleService.startAsyncPVP(
                accountId,
                teamUnits,
                enemyAccountIdList,
                true,
                Collections.emptyList());
        //
        br.setCurrentBattleSessionId(battleSession.getId());
        brawlRepository.save(br);
        //
        List<PlayerBaseInfo> enemies = new ArrayList<>();
        enemyAccountIdList.forEach((id) -> {
            enemies.add(compositePlayerService.getPlayerBaseInfo(id));
        });
        return new BrawlOverall(
                br,
                getTeamMembers(accountId),
                enemies,
                new BattleResponse(battleSession.getId(), battleSession.getBattleDirector().getBattleResult()));
    }

    public BrawlOverall finish(long accountId) {
        checkPlayerLevel(accountId);
        BrawlRecord br = findOrCreate(accountId);
        if (br.getStatus() != BrawlStatus.IN_CHALLENGE) {
            throw BrawlException.brawlStatusIncorrect();
        }
        //
        boolean isChallengeSuccess = false;
        if (br.getCurrentBattleSessionId() != 0) {
            BattleSession battleSession = battleRepository.findById(br.getCurrentBattleSessionId())
                    .orElse(null);
            if (battleSession != null) {
                BattleDirector bd = battleSession.getBattleDirector();
                if (!bd.isBattleEnd()) {
                    bd.finishBattleByAutoNextTurn();
                }
                if (bd.getBattleResult().getStatistics().getWinStance() == Unit.Stance.STANCE_RED) {
                    isChallengeSuccess = true;
                }
            }
        }
        //
        br.setCurrentBattleSessionId(0);
        if (isChallengeSuccess) {
            br.setCurrentStage(br.getCurrentStage() + 1);
            if (br.getCurrentStage() + 1 >= resourceContext.getLoader(BrawlStageInfo.class).getAll().size()) {
                br.setStatus(BrawlStatus.END);
            }
        } else {
            br.setBrawlCount(br.getBrawlCount() - 1);
            if (br.getBrawlCount() < 1) {
                br.setStatus(BrawlStatus.END);
            }
        }
        br = brawlRepository.save(br);
        //
        return new BrawlOverall(
                br,
                getTeamMembers(accountId),
                isChallengeSuccess);
    }

    public BrawlOverall award(long accountId) {
        checkPlayerLevel(accountId);
        BrawlRecord br = findOrCreate(accountId);
        if (br.getStatus() != BrawlStatus.END) {
            throw BrawlException.brawlStatusIncorrect();
        }
        //
        BrawlStageInfo bsi = resourceContext.getLoader(BrawlStageInfo.class).get(br.getCurrentStage());
        long selfFc = compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getFc();
        long awardAmount = Math.max((long) ((Math.min((double) selfFc / 70000, 1.0) * 0.75 + 0.25) * bsi.getAwardAmount()), 4);
        currencyService.increaseCurrency(accountId, CurrencyConstants.ID_斗战点, awardAmount, CurrencyConstants.PURPOSE_INCREMENT_乱斗大会产出);
        if (bsi.getExtraEnergyAward() > 0) {
            currencyService.increaseCurrency(accountId, CurrencyConstants.ID_能量, bsi.getExtraEnergyAward(), CurrencyConstants.PURPOSE_INCREMENT_乱斗大会产出);
        }
        eventPublisher.publishEvent(new BrawlEndEvent(this, br));
        if (br.getCurrentStage() + 1 >= resourceContext.getLoader(BrawlStageInfo.class).getAll().size()) {
            chatService.sendSystemMessage(ChatConstants.SERVICE_ID_UNDIFINED, ChatMessage.createTemplateMessage(
                    BrawlConstants.ALL_CLEAR_BROADCAST_ID,
                    ImmutableMap.of("playerName", compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerName())
            ));
        }
        //
        br.setStatus(BrawlStatus.NOT_START);
        br = brawlRepository.save(br);
        //
        return new BrawlOverall(
                br,
                getTeamMembers(accountId),
                awardAmount);
    }

    @Override
    public void dailyReset() {
        brawlRepository.resetResetCount();
    }

    private BrawlRecord findOrCreate(long accountId) {
        BrawlRecord br = brawlRepository.findByAccountId(accountId);
        if (br == null) {
            br = new BrawlRecord();
            br.setAccountId(accountId);
            br.setBrawlCount(BrawlConstants.MAX_BRAWL_COUNT);
            br.setCurrentStage(0);
            br.setCurrentBattleSessionId(0);
            br.setResetCount(1);
            br.setStatus(BrawlStatus.NOT_START);
            br.setTeamMember_1(0);
            br.setTeamMember_2(0);
            br = brawlRepository.save(br);
        }
        return br;
    }

    private void checkPlayerLevel(long accountId) {
        if (compositePlayerService.getPlayerBaseInfo(accountId).getPlayer().getPlayerLevel() < BrawlConstants.PLAYER_LEVEL_REQUIRE) {
            throw BrawlException.insufficientPlayerLevel();
        }
    }

    private UnitBuilder<?> createTeamMemberInternal(long accountId) {
        UnitBuilder<?> ub = UnitBuilder.create();
        battleUnitExporter.exportPlayer(playerRepository.findById(accountId).get(), ub, Unit.Stance.STANCE_RED, false, true);
        return ub;
    }

    private List<PlayerBaseInfo> getTeamMembers(long accountId) {
        List<PlayerBaseInfo> pbiList = new ArrayList<>();
        BrawlRecord br = findOrCreate(accountId);
        if (br.getTeamMember_1() != 0) {
            pbiList.add(compositePlayerService.getPlayerBaseInfo(br.getTeamMember_1()));
        }
        if (br.getTeamMember_2() != 0) {
            pbiList.add(compositePlayerService.getPlayerBaseInfo(br.getTeamMember_2()));
        }
        return pbiList;
    }

}
