/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.cultivation;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "cultivation_record")
@Data
public class CultivationRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "player_atk_level")
    private int playerAtkLevel;
    @Column(name = "player_atk_current_exp")
    private int playerAtkCurrentExp;
    @Column(name = "player_def_level")
    private int playerDefLevel;
    @Column(name = "player_def_current_exp")
    private int playerDefCurrentExp;
    //
    @Column(name = "pet_atk_level")
    private int petAtkLevel;
    @Column(name = "pet_atk_current_exp")
    private int petAtkCurrentExp;
    @Column(name = "pet_def_level")
    private int petDefLevel;
    @Column(name = "pet_def_current_exp")
    private int petDefCurrentExp;
    //
    @Column(name = "player_revive_level")
    private int playerReviveLevel;
    @Column(name = "player_revive_current_exp")
    private int playerReviveCurrentExp;

}
