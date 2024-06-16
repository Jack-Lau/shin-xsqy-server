/*
 * Created 2015-10-12 16:10:45
 */
package cn.com.yting.kxy.battle.action;

import cn.com.yting.kxy.battle.Unit;

/**
 *
 * @author Azige
 */
public class ActionChance implements Comparable<ActionChance> {

    public static final int MANUAL_INSTRUCTION_PRIORITY = 0;

    private final Unit actor;
    private Action action;
    private boolean special = false;
    private boolean immediately = false;

    public ActionChance(Unit unit) {
        this.actor = unit;
    }

    public Unit getActor() {
        return actor;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    /**
     * 是否为特殊行动机会，区分于通常一回合一次的常规行动机会。
     */
    public boolean isSpecial() {
        return special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    /**
     * 是否为紧急的行动机会，用于在队列最前插入一些特殊行动机会。
     *
     * @return
     */
    public boolean isImmediately() {
        return immediately;
    }

    public void setImmediately(boolean immediately) {
        this.immediately = immediately;
    }

    public double getPriority() {
        double skillPriority = 0;
        if (action != null && action.getType() == ActionType.USE_SKILL) {
            UseSkillAction useSkillAction = (UseSkillAction) action;
            skillPriority = useSkillAction.getSkill().getPriority();
            if (useSkillAction.isManualInstruction()){
                skillPriority += MANUAL_INSTRUCTION_PRIORITY;
            }
        }
        return actor.getSpeed() + skillPriority;
    }

    /**
     * 比较两个对象的顺序。 若两个对象的 {@link #isImmediately()} 值不同，则值为 true 的大于 false 的， 否则比较
     * {@link #getPriority()} 的值
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(ActionChance o) {
        if (this.isImmediately() != o.isImmediately()) {
            return this.isImmediately() ? 1 : -1;
        } else {
            return (int) Math.signum(this.getPriority() - o.getPriority());
        }
    }
}
