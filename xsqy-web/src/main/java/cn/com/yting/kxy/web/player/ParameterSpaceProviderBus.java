/*
 * Created 2018-9-27 16:59:52
 */
package cn.com.yting.kxy.web.player;

import java.util.List;

import cn.com.yting.kxy.core.parameter.AggregateParameterSpace;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.parameter.RootParameterSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
public class ParameterSpaceProviderBus {

    @Autowired
    @Lazy
    private List<ParameterSpaceProvider> parameterSpaceProviders;

    public RootParameterSpace createRootSpace(long accountId) {
        AggregateParameterSpace parameterSpace = new AggregateParameterSpace();
        RootParameterSpace rootSpace = parameterSpace.asRootParameterSpace();
        parameterSpaceProviders.forEach(provider -> {
            parameterSpace.getSubspaces().add(provider.createParameterSpace(accountId));
            ParameterSpace transformSpace = provider.createTransformParameterSpace(rootSpace);
            if (transformSpace != ParameterSpace.EMPTY) {
                parameterSpace.getSubspaces().add(transformSpace);
            }
        });
        return rootSpace;
    }

}
