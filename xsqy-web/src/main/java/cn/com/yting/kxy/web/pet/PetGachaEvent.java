/*
 * Created 2018-11-23 16:49:54
 */
package cn.com.yting.kxy.web.pet;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class PetGachaEvent extends KxyWebEvent {

    private final Pet pet;

    public PetGachaEvent(Object source, Pet pet) {
        super(source);
        this.pet = pet;
    }

    public Pet getPet() {
        return pet;
    }
}
