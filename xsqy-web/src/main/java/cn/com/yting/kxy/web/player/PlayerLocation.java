/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.player;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Entity
@Data
@Table(name = "player_location")
@WebMessageType
public class PlayerLocation implements Serializable {

    public enum DIRECTION {
        U, D, L, R, LU, LD, RU, RD
    }

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "map_id")
    private int mapId;
    @Column(name = "x_pos")
    private int xPos;
    @Column(name = "y_pos")
    private int yPos;
    @Column(name = "direction")
    private DIRECTION direction;

}
