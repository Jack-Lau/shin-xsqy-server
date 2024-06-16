/*
 * Created 2019-1-8 11:33:00
 */
package cn.com.yting.kxy.web.perk.resource;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class TalentTrainModelLoader extends XmlMapContainerResourceLoader<TalentTrainModel> {

    @Override
    public String getDefaultResourceName() {
        return "sTalentTrainModel.xml";
    }

    @Override
    protected void afterReload(ResourceContext context) {
        setMap(new TreeMap<>(getMap()));
    }

    public TalentTrainModel findModel(long currentProgress) {
        Iterator<Entry<Long, TalentTrainModel>> iter = getMap().entrySet().iterator();
        TalentTrainModel last = null;
        while (iter.hasNext()) {
            Entry<Long, TalentTrainModel> entry = iter.next();
            if (entry.getKey() > currentProgress) {
                break;
            }
            last = entry.getValue();
        }

        return last;
    }

    @Override
    public Class<TalentTrainModel> getSupportedClass() {
        return TalentTrainModel.class;
    }
}
