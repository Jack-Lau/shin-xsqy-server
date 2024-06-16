/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.skill;

import cn.com.yting.kxy.battle.buff.resource.BuffParam;
import cn.com.yting.kxy.core.resource.ClasspathScanResourceLoader;
import cn.com.yting.kxy.core.resource.ResourceContext;

/**
 *
 * @author Darkholme
 */
public class SkillPerformerLoader extends ClasspathScanResourceLoader<ResourceSkillPerformer> {

    @Override
    protected void afterReload(ResourceContext context) {
        getMap().values().stream()
            .filter(BuffAttachSkillPerformer.class::isInstance)
            .map(BuffAttachSkillPerformer.class::cast)
            .forEach(it -> it.setBuffReference(context.createReference(BuffParam.class, it.getBuffId())));
    }

    @Override
    public Class<ResourceSkillPerformer> getSupportedClass() {
        return ResourceSkillPerformer.class;
    }

}
