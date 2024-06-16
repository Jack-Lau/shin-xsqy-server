/*
 * Created 2018-12-11 11:35:40
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
public class PetFusionResult {

    private PetDetail petDetail;
    private long newAbility;
    private long droppedAbility;
}
