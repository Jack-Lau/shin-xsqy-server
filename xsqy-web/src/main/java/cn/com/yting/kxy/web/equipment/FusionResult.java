/*
 * Created 2018-11-26 15:42:32
 */
package cn.com.yting.kxy.web.equipment;

import java.util.List;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class FusionResult {

    private EquipmentDetail equipmentDetail;
    private boolean success;
    private List<Long> newEffectIds;
    private List<Long> droppedEffectIds;
}
