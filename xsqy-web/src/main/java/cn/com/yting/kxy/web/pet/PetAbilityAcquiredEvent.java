/*
 * Created 2018-11-23 18:03:15
 */
package cn.com.yting.kxy.web.pet;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class PetAbilityAcquiredEvent extends KxyWebEvent {

    private final Pet pet;
    private final long abilityId;

    public PetAbilityAcquiredEvent(Object source, Pet pet, long abilityId) {
        super(source);
        this.pet = pet;
        this.abilityId = abilityId;
    }

    public Pet getPet() {
        return pet;
    }

    public long getAbilityId() {
        return abilityId;
    }
}
