/*
 * Created 2015-10-8 15:34:31
 */
package cn.com.yting.kxy.battle;

import cn.com.yting.kxy.battle.BattleConstant.FURY_MODEL;
import cn.com.yting.kxy.battle.action.ActionChance;
import cn.com.yting.kxy.battle.action.Action;
import cn.com.yting.kxy.battle.action.UseSkillAction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.com.yting.kxy.battle.BattleResult.TurnInfo;
import cn.com.yting.kxy.battle.Unit.Stance;
import cn.com.yting.kxy.battle.Unit.UnitType;
import cn.com.yting.kxy.battle.action.ActionType;
import cn.com.yting.kxy.battle.event.ActionChanceEvent;
import cn.com.yting.kxy.battle.event.ActionChanceEvent.ActionChanceEventType;
import cn.com.yting.kxy.battle.event.BattleEvent;
import cn.com.yting.kxy.battle.event.BattleEvent.BattleEventType;
import cn.com.yting.kxy.battle.event.BeforeActionChanceEvent;
import cn.com.yting.kxy.battle.event.BuffEvent;
import cn.com.yting.kxy.battle.event.BuffEvent.BuffEventType;
import cn.com.yting.kxy.battle.event.CheckBattleEndEvent;
import cn.com.yting.kxy.battle.event.CheckBattleEndEvent.CheckBattleEndEventType;
import cn.com.yting.kxy.battle.event.DamageEvent;
import cn.com.yting.kxy.battle.event.UnitEvent;
import cn.com.yting.kxy.battle.event.UnitEvent.UnitEventType;
import cn.com.yting.kxy.battle.record.ActionRecord;
import cn.com.yting.kxy.battle.record.AffectRecord;
import cn.com.yting.kxy.battle.record.Recorder;
import cn.com.yting.kxy.battle.resource.FuryModel;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.battle.skill.resource.SkillParam;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceContextHolder;
import io.github.azige.mgxy.event.EventDispatcher;
import io.github.azige.mgxy.event.EventHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 战斗导演，战斗的整个流程控制由此类进行。提供各种事件用于触发战斗中的各种处理。
 *
 * @author Azige
 */
public class BattleDirector implements Recorder {

    private static final Logger logger = LoggerFactory.getLogger(BattleDirector.class);
    private final EventDispatcher eventDispatcher = new EventDispatcher();
    private final ResourceContext resourceContext = ResourceContextHolder.getResourceContext();

    //该场战斗的Unit总数量
    private int totalUnitCount = 0;
    private final boolean initialized = false;
    private boolean battleEnd = false;
    //怒火模型的类型
    private FURY_MODEL furyModel = FURY_MODEL.NONE;

    private final Party redParty;
    private final Party blueParty;

    private int turnCount = 0;
    private TurnInfo currTurnInfo;
    private List<ActionRecord> currActionRecords;
    private final BattleResult battleResult = new BattleResult();

    private final List<Unit> playerUnits = new ArrayList<>();
    private final Map<Unit, Action> playerActions = new HashMap<>();

    /**
     * 行动机会队列，决定了一回合中所有单位的行动顺序
     */
    private final Queue<ActionChance> actionChanceQueue = new PriorityQueue<>(Comparator.reverseOrder());
    /**
     * 错过行动的回收器，因为异常状态而错过行动，而有机会再次行动的，收集到这里。
     */
    private final Queue<ActionChance> missedActionChances = new ArrayDeque<>();

    public BattleDirector(Party redParty, Party blueParty) {
        this.redParty = redParty;
        this.blueParty = blueParty;
        init();
    }

    private void init() {
        eventDispatcher.addHandler(EventHandler.of(this::battleStartPartyAttendHandler, BattleEventType.BATTLE_START, 1000));
        getAllUnits().forEach(unit -> {
            initUnit(unit);
            unit.getBattlePetUnitQueue().forEach(this::initUnit);
        });
        redParty.getUnitMap().values().forEach(unit -> unit.setStance(Stance.STANCE_RED));
        blueParty.getUnitMap().values().forEach(unit -> unit.setStance(Stance.STANCE_BLUE));

        // 获取 Player 单位
        Unit[] players = getAllUnits()
                .filter(u -> u.getType() == UnitType.TYPE_PLAYER)
                .toArray(Unit[]::new);
        playerUnits.addAll(Arrays.asList(players));
    }

    private void initUnit(Unit unit) {
        totalUnitCount++;
        unit.setId(totalUnitCount);
    }

    private void saveUnitInitInfo() {
        battleResult.getUnitInitInfo().addAll(
                getAllUnits()
                        .map(Unit::toUnitInitInfo)
                        .collect(Collectors.toList())
        );
    }

    private void saveUnitStatus() {
        currTurnInfo.getUnitStatus().addAll(
                getAllUnits()
                        .map(Unit::toUnitStatus)
                        .collect(Collectors.toList())
        );
    }

    private void saveUnitStatusEndOfTurn() {
        getAllUnits()
                .map(Unit::toUnitStatus)
                .forEach(currTurnInfo.getEndOfTurnUnitStatus()::add);
    }

    private void genereteActionChanceQueue() {
        actionChanceQueue.clear();
        List<Unit> allUnitsList = getAllUnits().collect(Collectors.toList());
        getAllUnits()
                .map(unit -> {
                    ActionChance ac = new ActionChance(unit);
                    if (unit.getType() == UnitType.TYPE_PLAYER
                            && playerActions.get(unit) != null) {
                        ac.setAction(playerActions.get(unit));
                        playerActions.put(unit, null);
                    } else if (unit.getRobot() != null) {
                        ac.setAction(unit.getRobot().generateActionAtTurnStart(unit, allUnitsList));
                    }
                    return ac;
                })
                .forEach(actionChanceQueue::offer);
    }

    private void battleStartPartyAttendHandler(BattleEvent event) {
        partyAttend(redParty);
        partyAttend(blueParty);
    }

    /**
     * 直接进行整场战斗，不接受指令输入
     */
    public void oneshot() {
        run();
    }

    /**
     * 通知一个单位已出战，为其提供一些初始化工作并发布事件通知处理器
     *
     * @param unit
     */
    public void unitAttend(Unit unit) {
        unit.getSkills().forEach(skill -> skill.onUnitAttending(this, unit));
        eventDispatcher.enqueueEvent(new UnitEvent(UnitEventType.UNIT_ATTENDING, this, unit));
    }

    /**
     * 战斗开始前预处理队伍中的单位：分配位置，分配出战宠物
     *
     * @param party
     */
    private void partyAttend(Party party) {
        new HashSet<>(party.getUnitMap().keySet()).stream().forEach((Integer position) -> {
            Unit unit = party.getUnitMap().get(position);
            unitAttend(unit);
            unit.setPosition(position);
            if (!unit.getBattlePetUnitQueue().isEmpty()) {
                Unit petUnit;
                petUnit = unit.getBattlePetUnitQueue().poll();
                int petPosition = position + BattleConstant.PET_POSITION_OFFSET;
                petUnit.setPosition(petPosition);
                party.getUnitMap().put(petPosition, petUnit);
                unitAttend(petUnit);
            }
        });
    }

    public void battleStart() {
        if (initialized) {
            throw new IllegalStateException("战斗已经开始");
        }
        currActionRecords = battleResult.getBattleStartAction();
        eventDispatcher.enqueueEvent(new BattleEvent(BattleEventType.BATTLE_START, this));
        saveUnitInitInfo();
    }

    public void finishBattleByAutoNextTurn() {
        for (int i = 0; i < 1000; i++) {
            if (nextTurn()) {
                return;
            }
        }
    }

    public void finishBattle(Stance winStance) {
        battleEnd = true;
        if (currTurnInfo != null) {
            currTurnInfo.setBattleEnd(battleEnd);
        }
        //
        battleResult.getStatistics().setWinStance(winStance);
        battleResult.getStatistics().setTurnCount(turnCount);
        battleResult.getStatistics().setRedParty(redParty);
        battleResult.getStatistics().setBlueParty(blueParty);
        //
        eventDispatcher.enqueueEvent(new BattleEvent(BattleEventType.BATTLE_END, this));
    }

    /**
     *
     * @return 战斗是否结束
     */
    public boolean nextTurn() {
        if (battleEnd) {
            throw new IllegalStateException("战斗已经结束");
        }
        boolean battleFinished = false;
        CheckBattleEndEvent checkBattleEndEvent = null;

        notEnd:
        {
            turnCount++;
            //清除错过的行动机会
            missedActionChances.clear();
            //怒火层数的处理
            handleFuryModel(turnCount);
            //
            currTurnInfo = new TurnInfo();
            currTurnInfo.setTurnCount(turnCount);
            battleResult.getTurnInfo().add(currTurnInfo);
            currActionRecords = currTurnInfo.getActionRecord();

            saveUnitStatus();

            eventDispatcher.enqueueEvent(new BattleEvent(BattleEventType.TURN_START, this));

            genereteActionChanceQueue();

            while (!actionChanceQueue.isEmpty()) {

                eventDispatcher.enqueueEvent(new BeforeActionChanceEvent(this, actionChanceQueue));

                ActionChance actionChance = actionChanceQueue.poll();

                processActionChance(actionChance);

                checkBattleEndEvent = new CheckBattleEndEvent(CheckBattleEndEventType.CHECK_BATTLE_END_AT_ACTION_END, this);
                eventDispatcher.enqueueEvent(checkBattleEndEvent);
                battleFinished = checkBattleEndEvent.isBattleEnd();
                if (battleFinished) {
                    break notEnd;
                }
            }

            while (!missedActionChances.isEmpty()
                    && missedActionChances.stream().anyMatch(ac -> !ac.getActor().isHpZero())) {

                eventDispatcher.enqueueEvent(new BeforeActionChanceEvent(this, missedActionChances));

                ActionChance actionChance = missedActionChances.poll();

                processActionChance(actionChance);

                checkBattleEndEvent = new CheckBattleEndEvent(CheckBattleEndEventType.CHECK_BATTLE_END_AT_ACTION_END, this);
                eventDispatcher.enqueueEvent(checkBattleEndEvent);
                battleFinished = checkBattleEndEvent.isBattleEnd();
                if (battleFinished) {
                    break notEnd;
                }
            }

            eventDispatcher.enqueueEvent(new BattleEvent(BattleEventType.TURN_END, this));

            saveUnitStatusEndOfTurn();

            checkBattleEndEvent = new CheckBattleEndEvent(CheckBattleEndEventType.CHECK_BATTLE_END_AT_TURN_END, this);
            eventDispatcher.enqueueEvent(checkBattleEndEvent);
            battleFinished = checkBattleEndEvent.isBattleEnd();
            if (battleFinished) {
                break notEnd;
            }
            return false;
        }

        saveUnitStatusEndOfTurn();

        this.battleEnd = true;
        currTurnInfo.setBattleEnd(this.battleEnd);
        //
        battleResult.getStatistics().setWinStance(checkBattleEndEvent.getWinStance());
        battleResult.getStatistics().setTurnCount(turnCount);
        battleResult.getStatistics().setRedParty(redParty);
        battleResult.getStatistics().setBlueParty(blueParty);
        //
        eventDispatcher.enqueueEvent(new BattleEvent(BattleEventType.BATTLE_END, this));

        return true;
    }

    public void run() {
        battleStart();

        boolean battleFinished = false;
        while (!battleFinished) {
            battleFinished = nextTurn();
        }
    }

    private void processActionChance(ActionChance actionChance) {
        if (actionChance == null) {
            return;
        }

        if (actionChance.getAction() == null) {
            Robot robot = actionChance.getActor().getRobot();
            if (robot != null) {
                Action action = robot.generateActionAtActionStart(actionChance.getActor(), getAllUnits().collect(Collectors.toList()));
                actionChance.setAction(action);
            }
        }
        eventDispatcher.enqueueEvent(new ActionChanceEvent(ActionChanceEventType.ACTION_MODIFY, this, actionChance));
        // 如果行动修正后行动仍为空，则跳过行动处理
        if (actionChance.getAction() == null) {
            return;
        }

        // 处理行动否决
        boolean actionDenied = false;
        // 因为已经不能战斗而否决行动
        if (actionChance.getActor().isHpZero()) {
            if (!actionChance.isSpecial()) {
                missedActionChances.offer(actionChance);
            }
            actionDenied = true;
        }
        // 因为不可使用固有技而否决行动
        if (actionChance.getAction().getType() == ActionType.USE_SKILL) {
            UseSkillAction action = (UseSkillAction) actionChance.getAction();
            if (action.getSkill().getType() == SkillParam.SkillType.NORMAL && actionChance.getActor().getParameter(ParameterNameConstants.无法使用固有技).getValue() > 0) {
                actionDenied = true;
            }
        }

        if (!actionDenied) {
            eventDispatcher.enqueueEvent(new ActionChanceEvent(ActionChanceEventType.ACTION_START, this, actionChance));
            if (actionChance.getAction() != null) {
                ActionType actionType = actionChance.getAction().getType();
                if (actionType.equals(ActionType.USE_SKILL)) {
                    UseSkillAction action = (UseSkillAction) actionChance.getAction();
                    Unit source = actionChance.getActor();
                    action.perform(source, this, getAllUnits().collect(Collectors.toList()));
                }
            }

            eventDispatcher.enqueueEvent(new ActionChanceEvent(ActionChanceEventType.ACTION_END, this, actionChance));
        }
    }

    public Stream<Unit> getAllUnits() {
        return Stream.concat(redParty.getUnitMap().values().stream(), blueParty.getUnitMap().values().stream());
    }

    public Party getRedParty() {
        return redParty;
    }

    public Party getBlueParty() {
        return blueParty;
    }

    public Party getPartyOfUnit(Unit unit) {
        if (redParty.getUnitMap().values().contains(unit)) {
            return redParty;
        } else if (blueParty.getUnitMap().values().contains(unit)) {
            return blueParty;
        } else {
            return null;
        }
    }

    public Unit getPetOfUnit(Unit unit) {
        if (!Objects.equals(unit.getType(), UnitType.TYPE_PLAYER)) {
            throw new IllegalArgumentException("单位不是玩家单位");
        }
        return getPartyOfUnit(unit).getUnitMap().get(unit.getPosition() + BattleConstant.PET_POSITION_OFFSET);
    }

    /**
     * 获得的当前回合计数。此计数初始化为0，在每个回合开始前累加。
     *
     * @return 当前回合计数
     */
    public int getTurnCount() {
        return turnCount;
    }

    public Queue<ActionChance> getMissedActionChances() {
        return missedActionChances;
    }

    public BattleResult getBattleResult() {
        return battleResult;
    }

    @Override
    public void addActionRecord(ActionRecord actionRecord) {
        currActionRecords.add(actionRecord);
        fireActionRecordBasedEvent(actionRecord);
    }

    @Override
    public void addAffectRecord(AffectRecord affectRecord) {
        if (currActionRecords.isEmpty()) {
            currActionRecords.add(new ActionRecord());
        }
        List<List<AffectRecord>> affectRecordPack = currActionRecords.get(currActionRecords.size() - 1).affectRecordPack;
        if (affectRecordPack.isEmpty()) {
            createAffectRecordPack();
        }
        affectRecordPack.get(affectRecordPack.size() - 1).add(affectRecord);
        fireAffectRecordBasedEvent(affectRecord);
    }

    @Override
    public void createAffectRecordPack() {
        if (currActionRecords.isEmpty()) {
            currActionRecords.add(new ActionRecord());
        }
        List<List<AffectRecord>> affectRecordPack = currActionRecords.get(currActionRecords.size() - 1).affectRecordPack;
        affectRecordPack.add(new ArrayList<>());
    }

    public void fireActionRecordBasedEvent(ActionRecord actionRecord) {
        switch (actionRecord.type) {
            case BUFF_DECAY:
                eventDispatcher.enqueueEvent(new BuffEvent(BuffEventType.BUFF_DECAY, this, actionRecord.buffActor));
                break;
        }
    }

    public void fireAffectRecordBasedEvent(AffectRecord affectRecord) {
        switch (affectRecord.type) {
            case DAMAGE:
                eventDispatcher.enqueueEvent(new DamageEvent(this, affectRecord));
                break;
            case BUFF_ATTACH:
                if (affectRecord.buffs.size() > 0) {
                    eventDispatcher.enqueueEvent(new BuffEvent(BuffEventType.BUFF_ATTACH, this, affectRecord.buffs.get(0)));
                }
                break;
            case BUFF_DETACH:
                if (affectRecord.buffs.size() > 0) {
                    eventDispatcher.enqueueEvent(new BuffEvent(BuffEventType.BUFF_DETACH, this, affectRecord.buffs.get(0)));
                }
                break;
            case SUMMONEE:
                unitAttend(affectRecord.target);
                break;
        }
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    /**
     * 获得一个单位的友方单位集合，不包含此单位
     *
     * @param unit 要检索的单位
     * @return 该单位所属的友方单位集合，如果此单位不存在于战斗中则为空集合
     */
    public List<Unit> getAllies(Unit unit) {
        if (!getAllUnits().anyMatch(Predicate.isEqual(unit))) {
            return Collections.emptyList();
        }
        Party party = redParty.getUnitMap().values().contains(unit) ? redParty : blueParty;
        List<Unit> list = new ArrayList<>(party.getUnitMap().values());
        list.remove(unit);
        return list;
    }

    /**
     * 获得一个单位的敌对单位集合
     *
     * @param unit 要检索的单位
     * @return 该单位所属的敌对单位集合，如果此单位不存在于战斗中则为空集合
     */
    public List<Unit> getRivals(Unit unit) {
        if (!getAllUnits().anyMatch(Predicate.isEqual(unit))) {
            return Collections.emptyList();
        }
        Party party = redParty.getUnitMap().values().contains(unit) ? blueParty : redParty;
        return new ArrayList<>(party.getUnitMap().values());
    }

    /**
     *
     * @param sourceId
     * @param skillId
     * @param targetId
     * @throws IllegalArgumentException
     */
    public void setPlayerAction(long sourceId, long skillId, long targetId) {
        for (Unit player : playerUnits) {
            if (player.getSourceId() == sourceId) {
                Skill skill = player.getSkills().stream()
                        .filter(s -> s.getId() == skillId)
                        .findAny().orElseThrow(() -> new IllegalArgumentException("Player 单位不拥有 id=" + skillId + " 的技能"));
                Unit target = getAllUnits()
                        .filter(u -> u.getId() == targetId)
                        .findAny().orElseThrow(() -> new IllegalArgumentException("战斗中不存在 id=" + targetId + " 的战斗单位"));
                if (skill.canManualUse()) {
                    playerActions.put(player, new UseSkillAction(skill, target, true));
                }
                break;
            }
        }
    }

    private void handleFuryModel(int turnCount) {
        if (this.getFuryModel() != FURY_MODEL.NONE) {
            if (resourceContext.getLoader(FuryModel.class).exists(turnCount)) {
                FuryModel fm = resourceContext.getLoader(FuryModel.class).get(turnCount);
                getAllUnits().forEach((u) -> {
                    u.set怒火补正率(fm.getFuryRate(this.getFuryModel()));
                });
            }
        }
    }

    public boolean isBattleEnd() {
        return battleEnd;
    }

    /**
     * @return the fm
     */
    public FURY_MODEL getFuryModel() {
        return furyModel;
    }

    /**
     * @param furyModel the fm to set
     */
    public void setFuryModel(FURY_MODEL furyModel) {
        this.furyModel = furyModel;
    }

}
