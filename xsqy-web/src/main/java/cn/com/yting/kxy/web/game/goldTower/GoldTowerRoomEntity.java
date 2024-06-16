/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Entity
@Table(name = "gold_tower_room")
@Data
@WebMessageType
public class GoldTowerRoomEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "prototype_id", nullable = false)
    private long prototypeId;
    @Column(name = "floor_id", nullable = false)
    private long floorId;
    @Column(name = "treasure_count", nullable = false)
    private int treasureCount;
    @Column(name = "challengeParam_1")
    private String challengeParam_1;
    @Column(name = "challengeParam_2")
    private String challengeParam_2;
    @Column(name = "challengeParam_3")
    private String challengeParam_3;
    @Column(name = "waypoint_1")
    private Long waypoint_1;
    @Column(name = "waypointColor_1")
    private Long waypointColor_1;
    @Column(name = "waypoint_2")
    private Long waypoint_2;
    @Column(name = "waypointColor_2")
    private Long waypointColor_2;
    @Column(name = "waypoint_3")
    private Long waypoint_3;
    @Column(name = "waypointColor_3")
    private Long waypointColor_3;
    @Column(name = "waypoint_4")
    private Long waypoint_4;
    @Column(name = "waypointColor_4")
    private Long waypointColor_4;

}
