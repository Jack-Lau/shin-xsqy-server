/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.fashion;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.message.WebMessageType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Entity
@Table(name = "fashion_dye", indexes = {
    @Index(columnList = "account_id")
    ,
    @Index(columnList = "definition_id")
})
@Data
@WebMessageType
public class FashionDye implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "definition_id", nullable = false)
    private long definitionId;
    @Column(name = "dye_name")
    private String dyeName;

    @Column(name = "part_1_color")
    private int part_1_color;
    @Column(name = "part_1_saturation")
    private int part_1_saturation;
    @Column(name = "part_1_brightness")
    private int part_1_brightness;

    @Column(name = "part_2_color")
    private int part_2_color;
    @Column(name = "part_2_saturation")
    private int part_2_saturation;
    @Column(name = "part_2_brightness")
    private int part_2_brightness;

    @Column(name = "part_3_color")
    private int part_3_color;
    @Column(name = "part_3_saturation")
    private int part_3_saturation;
    @Column(name = "part_3_brightness")
    private int part_3_brightness;

    public void verifyOwner(long accountId) throws KxyWebException {
        if (this.accountId != accountId) {
            throw KxyWebException.unknown("不是染色方案的所有者");
        }
    }

    public Unit.FashionDye toBattleUnitFashionDye() {
        return new Unit.FashionDye(
                part_1_color, part_1_saturation, part_1_brightness,
                part_2_color, part_2_saturation, part_2_brightness,
                part_3_color, part_3_saturation, part_3_brightness);
    }

}
