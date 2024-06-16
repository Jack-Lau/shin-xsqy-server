/*
 * Created 2015-10-14 16:41:51
 */
package io.github.azige.mgxy.battle;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.BattleDirectorBuilder;
import cn.com.yting.kxy.battle.Unit.Stance;
import cn.com.yting.kxy.battle.Unit.UnitType;
import cn.com.yting.kxy.battle.record.ActionRecord;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.robot.RobotLoader;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.battle.skill.resource.SkillParam;
import cn.com.yting.kxy.battle.skill.resource.SkillParamLoader;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.resource.AutoScanResourceContext;
import cn.com.yting.kxy.core.util.JsonUtils;
import groovy.util.DelegatingScript;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Azige
 */
public class BattleDirectorTest {

    private BattleDirector bd;
    private BattleDirectorBuilder bdb;

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp() throws URISyntaxException, CompilationFailedException, IOException {
        AutoScanResourceContext resourceContext = new AutoScanResourceContext();
        Skill attackSkill = resourceContext.getLoader(SkillParam.class).get(100001).createSkill(0);
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setSourceEncoding("UTF-8");
        cc.setScriptBaseClass(DelegatingScript.class.getName());
        RobotLoader robotLoader = (RobotLoader) (resourceContext.getLoader(Robot.class));
        SkillParamLoader skillParamLoader = (SkillParamLoader) (resourceContext.getLoader(SkillParam.class));

        bdb = new BattleDirectorBuilder()
                .redParty()
                /**/
                .unit(1)
                .name("大唐官府")
                .hp(150000)
                .parameter(ParameterNameConstants.物伤, 2333333)
                .parameter(ParameterNameConstants.物防, 254)
                .parameter(ParameterNameConstants.法伤, 15340)
                .parameter(ParameterNameConstants.法防, 254)
                .parameter(ParameterNameConstants.速度, 485)
                .parameter(ParameterNameConstants.法术波动上限, 1)
                .parameter(ParameterNameConstants.法术波动下限, 1)
                //                .parameter(ParameterNameConstants.连击率, 1)
                //                .parameter(ParameterNameConstants.吸血率, 1)
                //                .parameter(ParameterNameConstants.暴击率, 1)
                //                .parameter(ParameterNameConstants.暴击效果, 2)
                .parameter(ParameterNameConstants.怒气消耗率, 1)
                .parameter(ParameterNameConstants.受伤怒气回复率, 1)
                .parameter(ParameterNameConstants.回合结束获得怒气, 10)
                .parameter(ParameterNameConstants.回春等级, 100)
                .parameter(ParameterNameConstants.五庄奇穴6_乙, 20)
                .skill(attackSkill)
                .skill(skillParamLoader.get(101201).createSkill(100))
                .skill(skillParamLoader.get(101301).createSkill(100))
                .skill(skillParamLoader.get(101501).createSkill(100))
                .skill(skillParamLoader.get(101101).createSkill(100))
                .skill(skillParamLoader.get(101401).createSkill(100))
                //
                .skill(skillParamLoader.get(102401).createSkill(100))
                .skill(skillParamLoader.get(102301).createSkill(100))
                .skill(skillParamLoader.get(102601).createSkill(100))
                .skill(skillParamLoader.get(102201).createSkill(100))
                .skill(skillParamLoader.get(102701).createSkill(100))
                .skill(skillParamLoader.get(102702).createSkill(100))
                //
                .skill(skillParamLoader.get(103101).createSkill(100))
                .skill(skillParamLoader.get(103201).createSkill(100))
                .skill(skillParamLoader.get(103301).createSkill(100))
                .skill(skillParamLoader.get(103501).createSkill(100))
                .skill(skillParamLoader.get(103701).createSkill(100))
                //
                .skill(skillParamLoader.get(104101).createSkill(100))
                .skill(skillParamLoader.get(104201).createSkill(100))
                .skill(skillParamLoader.get(104501).createSkill(100))
                .skill(skillParamLoader.get(104601).createSkill(100))
                .skill(skillParamLoader.get(104701).createSkill(100))
                //
                .stance(Stance.STANCE_RED)
                .type(UnitType.TYPE_PLAYER)
                .robot(robotLoader.get(109403))
                .andParty()
                /**/
                //                .unit(4)
                //                .name("泡泡")
                //                .hp(6000)
                //                .parameter(ParameterNameConstants.物伤, 432)
                //                .parameter(ParameterNameConstants.物防, 195)
                //                .parameter(ParameterNameConstants.速度, 398)
                //                .attackSkill()
                //                .attackRobot()
                //                .stance(Stance.STANCE_PLAYER)
                //                .type(UnitType.TYPE_PET)
                //                .andParty()
                //                /**/
                //                .unit(5)
                //                .name("泡泡")
                //                .hp(6000)
                //                .parameter(ParameterNameConstants.物伤, 432)
                //                .parameter(ParameterNameConstants.物防, 195)
                //                .parameter(ParameterNameConstants.速度, 398)
                //                .attackSkill()
                //                .attackRobot()
                //                .stance(Stance.STANCE_PLAYER)
                //                .type(UnitType.TYPE_PET)
                //                .andParty()
                //                /**/
                //                .unit(6)
                //                .name("泡泡")
                //                .hp(6000)
                //                .parameter(ParameterNameConstants.物伤, 432)
                //                .parameter(ParameterNameConstants.物防, 195)
                //                .parameter(ParameterNameConstants.速度, 398)
                //                .attackSkill()
                //                .attackRobot()
                //                .stance(Stance.STANCE_PLAYER)
                //                .type(UnitType.TYPE_PET)
                //                .andParty()
                /**/
                .andBattle()
                .blueParty()
                /**/
                .unit(1)
                .name("巨蛙")
                .hp(80000)
                .parameter(ParameterNameConstants.物伤, 1291)
                .parameter(ParameterNameConstants.物防, 254)
                .parameter(ParameterNameConstants.法防, 1600)
                .parameter(ParameterNameConstants.速度, 432)
                .attackSkill()
                .attackRobot()
                .stance(Stance.STANCE_BLUE)
                .type(UnitType.TYPE_MONSTER)
                .flyable()
                .andParty()
                /**/
                .unit(2)
                .name("海毛虫")
                .hp(8000)
                .parameter(ParameterNameConstants.物伤, 1326)
                .parameter(ParameterNameConstants.物防, 210)
                .parameter(ParameterNameConstants.法防, 1600)
                .parameter(ParameterNameConstants.速度, 405)
                .attackSkill()
                .attackRobot()
                .stance(Stance.STANCE_BLUE)
                .type(UnitType.TYPE_MONSTER)
                .flyable()
                .andParty()
                /**/
                .unit(3)
                .name("大海龟")
                .hp(8000)
                .parameter(ParameterNameConstants.物伤, 1372)
                .parameter(ParameterNameConstants.物防, 210)
                .parameter(ParameterNameConstants.法防, 1600)
                .parameter(ParameterNameConstants.速度, 350)
                .attackSkill()
                .attackRobot()
                .stance(Stance.STANCE_BLUE)
                .type(UnitType.TYPE_MONSTER)
                .flyable()
                .andParty()
                /**/
                .unit(4)
                .name("野猪")
                .hp(8000)
                .parameter(ParameterNameConstants.物伤, 1372)
                .parameter(ParameterNameConstants.物防, 210)
                .parameter(ParameterNameConstants.法防, 1600)
                .parameter(ParameterNameConstants.速度, 350)
                .attackSkill()
                .attackRobot()
                .stance(Stance.STANCE_BLUE)
                .type(UnitType.TYPE_MONSTER)
                .flyable()
                .andParty()
                /**/
                .unit(5)
                .name("树怪")
                .hp(8000)
                .parameter(ParameterNameConstants.物伤, 1372)
                .parameter(ParameterNameConstants.物防, 210)
                .parameter(ParameterNameConstants.法防, 1600)
                .parameter(ParameterNameConstants.速度, 350)
                .attackSkill()
                .attackRobot()
                .stance(Stance.STANCE_BLUE)
                .type(UnitType.TYPE_MONSTER)
                .flyable()
                .andParty()
                /**/
                .unit(6)
                .name("老虎")
                .hp(8000)
                .parameter(ParameterNameConstants.物伤, 1372)
                .parameter(ParameterNameConstants.物防, 210)
                .parameter(ParameterNameConstants.法防, 1600)
                .parameter(ParameterNameConstants.速度, 350)
                .attackSkill()
                .attackRobot()
                .stance(Stance.STANCE_BLUE)
                .type(UnitType.TYPE_MONSTER)
                .flyable()
                .andParty()
                /**/
                .andBattle();
        bd = bdb.build();
    }

    @Test
    public void testOne() throws IOException {
        bd.oneshot();
        bd.getBattleResult().getTurnInfo().forEach(tr -> {
            System.out.println("开始第" + tr.getTurnCount() + "回合");
            printActionRecord(tr.getActionRecord());
        });
        String recordJson = JsonUtils.toJson(bd.getBattleResult());
        System.out.println(recordJson);
        try (BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("target/battle-record-demo.json"), Charset.forName("UTF-8")))) {
            output.write(recordJson);
        }
    }

//    @Test
//    public void testUnitInfo(){
//        bd.oneshot();
//        System.out.println(JsonUtil.toJson(bd.getUnitInitInfo()));
//        System.out.println(JsonUtil.toJson(bd.getUnitTurnInfo()));
//    }
    private void printActionRecord(List<ActionRecord> actionRecord) {
        actionRecord.forEach(System.out::println);
    }
}
