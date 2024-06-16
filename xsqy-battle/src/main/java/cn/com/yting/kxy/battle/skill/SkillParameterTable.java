/*
 * Created 2018-8-18 11:35:39
 */
package cn.com.yting.kxy.battle.skill;

import cn.com.yting.kxy.battle.skill.resource.SkillParam.ElementType;
import lombok.Builder;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@Builder
public class SkillParameterTable {

    public static final double default命中率 = 0.95;

    private long id;

    //
    private ElementType 元素类型;

    //
    private double 固定命中率;
    private double 基础命中率;

    private double 基础系数;
    private double 回数系数;

    private double 基础值等级系数;
    private double 基础值常数;

    private double 多目标衰减系数;
    private double 多目标衰减比例;

    //
    private double 防御穿透率;
    private double 额外暴击率;
    private double 额外暴击效果;
    private double 额外连击率;

    //
    private double 易伤率;
    private double 易疗率;

    //
    private double 额外物伤;
    private double 额外法伤;

}
