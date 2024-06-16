/*
 * Created 2018-11-23 17:55:52
 */
package cn.com.yting.kxy.web.pet;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class PetEnhancedEvent extends KxyWebEvent {

    private final PetEnhanceResult result;

    public PetEnhancedEvent(Object source, PetEnhanceResult result) {
        super(source);
        this.result = result;
    }

    public PetEnhanceResult getResult() {
        return result;
    }
}
