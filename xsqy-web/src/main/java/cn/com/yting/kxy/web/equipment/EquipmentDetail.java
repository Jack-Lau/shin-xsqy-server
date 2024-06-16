/*
 * Created 2018-10-16 11:56:20
 */
package cn.com.yting.kxy.web.equipment;

import java.util.List;

import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class EquipmentDetail {

    private Equipment equipment;
    private List<Parameter> parameters;

    public EquipmentDetail(Equipment equipment, List<Parameter> parameters) {
        this.equipment = equipment;
        this.parameters = parameters;
    }

}
