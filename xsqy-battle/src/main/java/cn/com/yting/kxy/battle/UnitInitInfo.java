/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle;

import cn.com.yting.kxy.battle.Unit.FashionDye;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Darkholme
 */
public class UnitInitInfo {

    public long id;
    public long sourceId;
    public long unitDescriptorId;
    public long schoolId;
    //
    public Unit.UnitType type;
    public Unit.Stance stance;
    public int position;
    public boolean hpVisible;
    //
    public String name;
    public long prefabId;
    public Long weaponPrefabId;
    public Long titleId;
    public Long fashionId;
    public FashionDye fashionDye;
    public double modelScale;
    //
    public long maxHp;
    public long maxSp;
    public long hp;
    public long sp;
    public double 怒气消耗率;
    //
    public List<Long> skillIds = new ArrayList<>();

}
