/*
 * Created 2018-10-12 12:54:20
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
public class PetGachaLog {

    private String playerName;
    private long playerPrefabId;
    private long petDefinitionId;
    private long eventTime;
}
