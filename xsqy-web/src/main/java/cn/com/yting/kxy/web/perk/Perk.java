/*
 * Created 2019-1-7 17:38:56
 */
package cn.com.yting.kxy.web.perk;

import java.util.List;

import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class Perk {

    private int rank;
    private PerkSelection selection;
    @JsonIgnore
    private ParameterSpace parameterSpace;

    public List<Parameter> getParameters() {
        return parameterSpace.asRootParameterSpace().toParameters();
    }
}
