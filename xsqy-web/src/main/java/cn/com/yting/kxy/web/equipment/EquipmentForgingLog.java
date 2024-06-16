/*
 * Created 2018-9-21 17:35:16
 */
package cn.com.yting.kxy.web.equipment;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class EquipmentForgingLog {

    private String playerName;
    private long playerPrefabId;
    private long equipmentDefinitionId;
    private long eventTime;
}
