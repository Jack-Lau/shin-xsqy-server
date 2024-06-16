/*
 * Created 2018-10-11 10:52:51
 */
package cn.com.yting.kxy.web.pet;

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
public class PetDetail {

    private Pet pet;
    private List<Parameter> parameters;

    public PetDetail(Pet pet, List<Parameter> parameters) {
        this.pet = pet;
        this.parameters = parameters;
    }

}
