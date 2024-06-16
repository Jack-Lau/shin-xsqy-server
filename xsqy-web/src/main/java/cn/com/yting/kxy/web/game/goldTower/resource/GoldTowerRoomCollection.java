/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.goldTower.resource;

import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
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
public class GoldTowerRoomCollection implements Resource {

    @XmlAttribute
    private long id;
    @XmlElements(
            @XmlElement(name = "room", type = RoomDescription.class)
    )
    private List<RoomDescription> room;

    private RandomSelector<Long> randomSelector = null;

    public RandomSelector<Long> getRandomSelector() {
        if (randomSelector == null) {
            RandomSelectorBuilder<Long> collectionBuilder = RandomSelector.<Long>builder();
            for (RoomDescription rd : room) {
                collectionBuilder.add(rd.getId(), rd.getProbability());
            }
            randomSelector = collectionBuilder.build(RandomSelectType.DEPENDENT);
        }
        return randomSelector;
    }

    @Getter
    public static class RoomDescription {

        @XmlElement
        private long id;
        @XmlElement
        private double probability;

    }

}
