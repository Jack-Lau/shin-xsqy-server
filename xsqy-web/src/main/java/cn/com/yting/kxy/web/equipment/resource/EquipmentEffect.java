/*
 * Created 2018-9-19 16:52:42
 */
package cn.com.yting.kxy.web.equipment.resource;

import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.resource.Resource;

/**
 *
 * @author Azige
 */
public abstract class EquipmentEffect implements Resource {

    public abstract String getName();

    public abstract ParameterSpace getParameterSpace();

}
