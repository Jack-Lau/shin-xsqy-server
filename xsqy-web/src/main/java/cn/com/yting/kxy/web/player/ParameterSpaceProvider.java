/*
 * Created 2018-8-10 11:23:50
 */
package cn.com.yting.kxy.web.player;

import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.RootParameterSpace;

/**
 *
 * @author Azige
 */
public interface ParameterSpaceProvider {

    ParameterSpace createParameterSpace(long accountId);

    default ParameterSpace createTransformParameterSpace(RootParameterSpace rootSpace) {
        return ParameterSpace.EMPTY;
    }
}
