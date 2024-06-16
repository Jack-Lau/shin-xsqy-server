/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.buff.resource;

import cn.com.yting.kxy.battle.buff.BuffDecayType;
import cn.com.yting.kxy.battle.buff.BuffMerger;
import cn.com.yting.kxy.battle.buff.BuffMergers;
import cn.com.yting.kxy.battle.buff.BuffPrototype;
import cn.com.yting.kxy.battle.buff.BuffPrototype.Type;
import cn.com.yting.kxy.core.resource.Resource;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 *
 * @author Darkholme
 */
@Getter
public class BuffParam implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int type;
    @XmlElement
    private int attenuation;
    @XmlElement
    private int merge;

    private BuffPrototype prototype;

    public BuffPrototype getPrototype() {
        if (prototype == null) {
            Type type;
            BuffDecayType decayType;
            BuffMerger merger;
            switch (this.type) {
                case 0:
                    type = Type.默认;
                    break;
                case 1:
                    type = Type.增益;
                    break;
                case 2:
                    type = Type.减益;
                    break;
                case 3:
                    type = Type.控制;
                    break;
                default:
                    type = Type.默认;
                    break;
            }
            switch (this.attenuation) {
                case 0:
                    decayType = BuffDecayType.NONE;
                    break;
                case 1:
                    decayType = BuffDecayType.TURN_END;
                    break;
                default:
                    decayType = BuffDecayType.TURN_END;
                    break;
            }
            switch (this.merge) {
                case 0:
                    merger = BuffMergers.rejector();
                    break;
                case 1:
                    merger = BuffMergers.parameterMerger();
                    break;
                case 2:
                    merger = BuffMergers.countdownMerger();
                    break;
                case 3:
                    merger = BuffMergers.overrider();
                    break;
                case 4:
                    merger = BuffMergers.maximizer();
                    break;
                default:
                    merger = BuffMergers.rejector();
                    break;
            }
            prototype = new BuffPrototype(id, name, type, decayType, merger);
        }
        return prototype;
    }

}
