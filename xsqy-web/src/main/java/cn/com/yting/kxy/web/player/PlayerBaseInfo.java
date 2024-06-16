/*
 * Created 2018-9-28 15:49:31
 */
package cn.com.yting.kxy.web.player;

import cn.com.yting.kxy.web.fashion.FashionDye;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class PlayerBaseInfo {

    private Player player;
    private Long schoolId;
    private Long weaponId;
    private Long titleDefinitionId;

    private Long fashionDefinitionId;
    private FashionDye fashionDye;
    private boolean shenxing;

}
