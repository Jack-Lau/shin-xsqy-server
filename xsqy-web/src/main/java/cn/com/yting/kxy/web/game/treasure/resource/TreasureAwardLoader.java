/*
 * Created 2018-11-2 15:37:37
 */
package cn.com.yting.kxy.web.game.treasure.resource;

import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.pool.PoolSelector;
import cn.com.yting.kxy.core.random.pool.PoolSelectorBuilder;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class TreasureAwardLoader extends XmlMapContainerResourceLoader<TreasureAward> {

    private PoolSelector poolSelector;

    @Override
    protected void afterReload(ResourceContext context) {
        PoolSelectorBuilder builder = new PoolSelectorBuilder();
        getAll().values().stream()
            .map(it -> it.toPoolSelectorElement())
            .forEach(builder::add);
        poolSelector = builder.build(RandomSelectType.DEPENDENT);
    }

    public PoolSelector getPoolSelector() {
        return poolSelector;
    }

    @Override
    public String getDefaultResourceName() {
        return "sTreasureAward.xml";
    }

    @Override
    public Class<TreasureAward> getSupportedClass() {
        return TreasureAward.class;
    }
}
