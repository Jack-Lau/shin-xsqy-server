/*
 * Created 2018-7-13 11:28:53
 */
package cn.com.yting.kxy.web.game.kuaibidazhuanpan.resource;

import java.io.InputStream;
import java.util.Map;

import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.pool.PoolSelector;
import cn.com.yting.kxy.core.random.pool.PoolSelectorBuilder;
import cn.com.yting.kxy.core.random.pool.PoolSelectorElement;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceLoaderUtils;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class KCWheelLoader extends XmlMapContainerResourceLoader<KCWheel> {

    private PoolSelector normalSelector;
    private PoolSelector lowLevelSelector;

    @Override
    public void reload(ResourceContext context, InputStream input) {
        super.reload(context, input);
        buildNormalSelector();
        buildLowLevelSelector();
    }

    @Override
    protected Map<Long, KCWheel> createMap(ResourceContext resourceContext, InputStream inputStream) {
        return ResourceLoaderUtils.extractDataMap(inputStream, KCWheel.class);
    }

    @Override
    public String getDefaultResourceName() {
        return "sKCWheel.xml";
    }

    @Override
    public Class<KCWheel> getSupportedClass() {
        return KCWheel.class;
    }

    public PoolSelector getNormalSelector() {
        return normalSelector;
    }

    public PoolSelector getLowLevelSelector() {
        return lowLevelSelector;
    }

    private void buildNormalSelector() {
        PoolSelectorBuilder builder = PoolSelector.builder();
        getAll().values().forEach(it -> builder.add(new PoolSelectorElement(it.currencyId, it.amount, it.normalProbability, it)));
        normalSelector = builder.build(RandomSelectType.DEPENDENT);
    }

    private void buildLowLevelSelector() {
        PoolSelectorBuilder builder = PoolSelector.builder();
        getAll().values().forEach(it -> builder.add(new PoolSelectorElement(it.currencyId, it.amount, it.lowLevelProbability, it)));
        lowLevelSelector = builder.build(RandomSelectType.DEPENDENT);
    }

}
