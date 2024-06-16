/*
 * Created 2018-9-19 16:07:49
 */
package cn.com.yting.kxy.web.equipment;

import java.util.List;

import cn.com.yting.kxy.web.equipment.resource.EquipmentStrengtheningStatus;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class EnhancingResult {

    private EquipmentDetail equipmentDetail;
    private List<Long> newEquipmentEffects;
    private EquipmentStrengtheningStatus status;
}
