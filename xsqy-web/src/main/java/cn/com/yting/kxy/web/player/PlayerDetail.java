/*
 * Created 2018-8-8 19:05:14
 */
package cn.com.yting.kxy.web.player;

import java.util.List;

import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.web.equipment.Equipment;
import cn.com.yting.kxy.web.fashion.Fashion;
import cn.com.yting.kxy.web.fashion.FashionDye;
import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.title.Title;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class PlayerDetail {

    private Player player;
    private Title title;
    private Fashion fashion;
    private FashionDye fashionDye;
    private Long schoolId;
    private PlayerRelation playerRelation;
    private List<Equipment> equipments;
    private List<Parameter> parameters;

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the title
     */
    public Title getTitle() {
        return title;
    }

    /**
     * @return the fashion
     */
    public Fashion getFashion() {
        return fashion;
    }

    /**
     * @return the fashionDye
     */
    public FashionDye getFashionDye() {
        return fashionDye;
    }

    /**
     * @return the schoolId
     */
    public Long getSchoolId() {
        return schoolId;
    }

    /**
     * @return the playerRelation
     */
    public PlayerRelation getPlayerRelation() {
        return playerRelation;
    }

    /**
     * @return the equipments
     */
    public List<Equipment> getEquipments() {
        return equipments;
    }

    /**
     * @return the parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }
}
