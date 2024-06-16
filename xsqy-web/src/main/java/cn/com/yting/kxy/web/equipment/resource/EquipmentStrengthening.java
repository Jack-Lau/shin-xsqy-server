/*
 * Created 2018-9-18 15:37:57
 */
package cn.com.yting.kxy.web.equipment.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
// z装备强化表.xlsx revision 11428
@Getter
public class EquipmentStrengthening implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long amount;
    @XmlElement
    private int success;
    @XmlElement
    private int unchanged;
    @XmlElement
    private int fail;
    @XmlElement
    private double ability;
    @XmlElement
    private double fc;

    private RandomSelector<EquipmentStrengtheningStatus> selector;

    public RandomSelector<EquipmentStrengtheningStatus> getSelector() {
        if (selector == null) {
            selector = RandomSelector.<EquipmentStrengtheningStatus>builder()
            .add(EquipmentStrengtheningStatus.SUCCESSFUL, success)
            .add(EquipmentStrengtheningStatus.UNCHANGED, unchanged)
            .add(EquipmentStrengtheningStatus.FAILED, fail)
            .build(RandomSelectType.DEPENDENT);
        }
        return selector;
    }
}
