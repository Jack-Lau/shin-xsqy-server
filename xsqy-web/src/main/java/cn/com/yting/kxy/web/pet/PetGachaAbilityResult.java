/*
 * Created 2018-10-22 18:17:01
 */
package cn.com.yting.kxy.web.pet;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class PetGachaAbilityResult {

    private Pet pet;
    private boolean success;
}
