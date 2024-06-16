/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower.resource;

import cn.com.yting.kxy.core.resource.Resource;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import lombok.Getter;

/**
 *
 * @author Darkholme
 */
@Getter
public class GoldTowerFloor implements Resource {

    @XmlAttribute
    private long id;
    @XmlElements(
            @XmlElement(name = "roomContainer", type = RoomContainer.class)
    )
    private List<RoomContainer> roomContainer;
    @XmlElement
    private int treasureLowerLimit;
    @XmlElement
    private int treasureUpperLimit;
    @XmlElement
    private long treasureAwardId;
    @XmlElement
    private Long broadcastId;

    @Getter
    public static class RoomContainer {

        @XmlElement
        private long selectRoomId;
        @XmlElement
        private double probability;

    }

}
