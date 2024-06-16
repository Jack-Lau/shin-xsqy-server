/*
 * Created 2018-9-14 11:05:06
 */
package cn.com.yting.kxy.battle.skill;

import cn.com.yting.kxy.battle.buff.resource.BuffParam;
import cn.com.yting.kxy.core.resource.ResourceReference;

/**
 *
 * @author Azige
 */
public interface BuffAttachSkillPerformer extends ResourceSkillPerformer {

    long getBuffId();

    void setBuffReference(ResourceReference<BuffParam> ref);
}
