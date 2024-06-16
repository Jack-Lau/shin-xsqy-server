/*
 * Created 2018-11-13 15:30:07
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
public class PetEnhanceResult {

    private Pet pet;
    private boolean success;
    private Long newAbilityId;
}
