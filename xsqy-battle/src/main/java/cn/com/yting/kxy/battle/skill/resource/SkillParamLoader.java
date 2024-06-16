/*
 * Created 2018-8-18 16:04:31
 */
package cn.com.yting.kxy.battle.skill.resource;

import java.io.InputStream;

import cn.com.yting.kxy.battle.skill.ResourceSkillPerformer;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class SkillParamLoader extends XmlMapContainerResourceLoader<SkillParam> {

    @Override
    public void reload(ResourceContext context, InputStream input) {
        super.reload(context, input);
        getMap().values().stream()
            .filter(it -> it.getTemplateType() == 4)
            .forEach(it -> {
                it.injectSpecialSkillPerformerRef(context.createReference(ResourceSkillPerformer.class, it.getId()));
            });
    }

    @Override
    public String getDefaultResourceName() {
        return "sSkillParam.xml";
    }

    @Override
    public Class<SkillParam> getSupportedClass() {
        return SkillParam.class;
    }

}
