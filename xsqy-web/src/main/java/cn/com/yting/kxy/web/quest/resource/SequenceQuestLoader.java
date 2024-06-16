/*
 * Created 2018-8-1 15:23:31
 */
package cn.com.yting.kxy.web.quest.resource;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class SequenceQuestLoader extends XmlMapContainerResourceLoader<SequenceQuest> {

    @Override
    public void reload(ResourceContext context, InputStream input) {
        super.reload(context, input);
        buildActivateIds(getMap());
    }

    @Override
    public String getDefaultResourceName() {
        return "sSequenceQuest.xml";
    }

    @Override
    public Class<SequenceQuest> getSupportedClass() {
        return SequenceQuest.class;
    }

    private static void buildActivateIds(Map<Long, SequenceQuest> map){
        map.values().forEach(prototype -> {
            Set<Long> activateIds = map.values().stream()
                .filter(p -> p.getPreRequireDependencyIds().contains(prototype.getId()))
                .map(p -> p.getId())
                .collect(Collectors.toSet());
            prototype.setActivateIds(activateIds);
        });
    }
}
