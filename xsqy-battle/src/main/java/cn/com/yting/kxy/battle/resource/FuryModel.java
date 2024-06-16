/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.resource;

import cn.com.yting.kxy.battle.BattleConstant.FURY_MODEL;
import cn.com.yting.kxy.core.resource.Resource;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 *
 * @author Darkholme
 */
@Getter
public class FuryModel implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private double model_1;
    @XmlElement
    private double model_2;
    @XmlElement
    private double model_3;
    @XmlElement
    private double model_4;

    public double getFuryRate(FURY_MODEL furyModel) {
        switch (furyModel) {
            case ASYNC_PVP_SINGLE:
                return model_1;
            case ASYNC_PVP_TEAM:
                return model_2;
            case SYNC_PVP_SINGLE:
                return model_3;
            case SYNC_PVP_TEAM:
                return model_4;
        }
        return 0.0;
    }

}
