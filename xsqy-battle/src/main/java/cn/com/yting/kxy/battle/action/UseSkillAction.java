/*
 * Created 2016-4-14 16:53:46
 */
package cn.com.yting.kxy.battle.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import cn.com.yting.kxy.battle.DamageValue;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.affect.Affect;
import cn.com.yting.kxy.battle.record.ActionRecord;
import cn.com.yting.kxy.battle.record.AffectRecord;
import cn.com.yting.kxy.battle.record.Recorder;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.random.RandomProvider;

/**
 *
 * @author Azige
 */
public class UseSkillAction implements Action {

    private static final long SKILL_四边静_ID = 101101;
    private static final long SKILL_万化归一诀_ID = 104101;
    private static final long SKILL_八方湮灭_ID = 104701;

    private final Skill skill;
    private Unit target;
    private boolean manualInstruction;
    private final List<ActionRecord> actionRecords = new ArrayList<>();

    public UseSkillAction(Skill skill, Unit target) {
        this(skill, target, false);
    }

    public UseSkillAction(Skill skill, Unit target, boolean manualInstruction) {
        this.skill = Objects.requireNonNull(skill);
        this.target = Objects.requireNonNull(target);
        this.manualInstruction = manualInstruction;
    }

    public Skill getSkill() {
        return skill;
    }

    public Unit getTarget() {
        return target;
    }

    public void setTarget(Unit target) {
        this.target = target;
    }

    public boolean isManualInstruction() {
        return manualInstruction;
    }

    @Override
    public ActionType getType() {
        return ActionType.USE_SKILL;
    }

    public void perform(Unit actor, Recorder recorder, List<Unit> allUnits) {

        // 连击判断
        int multiHitCount = 1;
        if (skill.canMultihit()) {
            double multihitPercent = actor.getParameter(ParameterNameConstants.连击率).getValue()
                    + skill.getSkillParameterTable().get额外连击率()
                    + skill.getExtraMultihitRate(actor);
            if (RandomProvider.getRandom().nextDouble() < multihitPercent) {
                multiHitCount += 1;
            }
        }

        // 连击处理
        for (int multiHit = 1; multiHit <= multiHitCount; multiHit++) {
            ActionRecord actionRecord = new ActionRecord();
            actionRecord.type = ActionRecord.ActionRecordType.USE_SKILL;
            actionRecord.actorId = actor.getId();
            actionRecord.actionId = skill.getId();

            // 消耗处理
            DamageValue cost = DamageValue.spOnly(actor.getSpCost(skill.getCost().getSp()));
            if (!actor.checkCost(cost)) {
                actionRecord.executeResult = ActionRecord.ExecuteResult.FAIL_NOTENOUGHCOST;
                recorder.addActionRecord(actionRecord);
                return;
            }
            // 五庄奇穴7甲
            if (skill.getId() == SKILL_八方湮灭_ID
                    && actor.getParameter(ParameterNameConstants.五庄奇穴7_甲).getValue() > 0) {
                cost = DamageValue.spOnly(actor.getSp().getValue());
            }
            actor.cost(cost);
            actionRecord.cost.sp = (long) cost.getSp();

            // 选择主目标
            List<Unit> targetableUnits = allUnits.stream()
                    .filter(u -> checkTarget(actor, u))
                    .collect(Collectors.toCollection(ArrayList::new));
            if (!checkTarget(actor, target)) {
                if (targetableUnits.isEmpty() || (skill.getMaxTargetCount(actor) == 1 && multiHitCount > 1)) {
                    actionRecord.executeResult = ActionRecord.ExecuteResult.FAIL_TARGETLOST;
                    recorder.addActionRecord(actionRecord);
                    return;
                } else {
                    int index = RandomProvider.getRandom().nextInt(targetableUnits.size());
                    target = targetableUnits.get(index);
                    targetableUnits.remove(index);
                }
            } else {
                targetableUnits.remove(target);
            }

            // 选择副目标
            List<Unit> secondaryTargets = new ArrayList<>();
            int maxTargetCount = skill.getMaxTargetCount(actor);
            int targetCount;
            if (maxTargetCount > 1) {
                targetableUnits = skill.selectSecondaryTargets(target, targetableUnits);
                int secondaryTargetCount = Math.min(maxTargetCount - 1, targetableUnits.size());
                secondaryTargets.addAll(targetableUnits.subList(0, secondaryTargetCount));
                targetCount = secondaryTargetCount + 1;
            } else {
                targetCount = 1;
            }

            //
            actionRecord.executeResult = ActionRecord.ExecuteResult.SUCCESS;

            // 处理技能
            actionRecord.processCount = skill.getMaxProcessCount(actor);
            recorder.addActionRecord(actionRecord);
            this.actionRecords.add(actionRecord);
            for (int processCount = 1; processCount <= actionRecord.processCount; processCount++) {
                recorder.createAffectRecordPack();
                for (Affect affect : skill.processMainTarget(actor, target, processCount, targetCount, cost)) {
                    AffectRecord record = affect.affect(target);
                    if (record != null) {
                        recorder.addAffectRecord(record);
                    }
                }
                // 四边静强化
                if (skill.getId() == SKILL_四边静_ID && actor.getParameter(ParameterNameConstants.四边静强化).getValue() > 0) {
                    for (Affect affect : skill.processMainTarget(actor, target, processCount, targetCount, cost)) {
                        AffectRecord record = affect.affect(target);
                        if (record != null) {
                            recorder.addAffectRecord(record);
                        }
                    }
                }
                // 通常流程
                if (skill.getId() != SKILL_万化归一诀_ID) {
                    for (Unit secondaryTarget : secondaryTargets) {
                        recorder.createAffectRecordPack();
                        for (Affect affect : skill.processSecondaryTarget(actor, secondaryTarget, processCount, targetCount, cost)) {
                            AffectRecord record = affect.affect(secondaryTarget);
                            if (record != null) {
                                recorder.addAffectRecord(record);
                            }
                        }
                    }
                } // 万化归一诀
                else {
                    int acturalTargetCount = skill.getMaxTargetCount(actor) - 1;
                    // 万化归一诀强化
                    if (skill.getId() == SKILL_万化归一诀_ID && actor.getParameter(ParameterNameConstants.万化归一诀强化).getValue() > 0) {
                        acturalTargetCount++;
                    }
                    for (int i = 0; i < acturalTargetCount; i++) {
                        List<Unit> targets = new ArrayList<>();
                        for (Unit u : allUnits) {
                            if (!actor.isAlly(u) && u.getHp().getRate() > 0) {
                                targets.add(u);
                            }
                        }
                        if (targets.size() < 1) {
                            break;
                        }
                        Unit target = targets.get(RandomProvider.getRandom().nextInt(targets.size()));
                        recorder.createAffectRecordPack();
                        for (Affect affect : skill.processSecondaryTarget(actor, target, processCount, i + 5, cost)) {
                            AffectRecord record = affect.affect(target);
                            if (record != null) {
                                recorder.addAffectRecord(record);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean checkTarget(Unit actor, Unit target) {
        return skill.checkTargetable(actor, target) && target.isTargetable(actor);
    }

    @Override
    public List<ActionRecord> getActionRecords() {
        return actionRecords;
    }

}
