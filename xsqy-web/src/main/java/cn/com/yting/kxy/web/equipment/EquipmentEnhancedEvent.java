/*
 * Created 2018-11-23 17:42:46
 */
package cn.com.yting.kxy.web.equipment;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class EquipmentEnhancedEvent extends KxyWebEvent {

    private final EnhancingResult result;

    public EquipmentEnhancedEvent(Object source, EnhancingResult result) {
        super(source);
        this.result = result;
    }

    public EnhancingResult getResult() {
        return result;
    }
}
