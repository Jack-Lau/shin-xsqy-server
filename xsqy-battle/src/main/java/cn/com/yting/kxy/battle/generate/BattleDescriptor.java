/*
 * Created 2016-1-14 15:32:54
 */
package cn.com.yting.kxy.battle.generate;

import cn.com.yting.kxy.battle.BattleDirectorBuilder;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceReference;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Data
public class BattleDescriptor implements Resource {

    private long id;
    private ResourceReference<PartyDescriptor> monsterPartyDescriptorRef;
    private boolean hpVisible;
    /**
     * 是否限制为单人战斗
     */
    private boolean singlePlayerLimited;

    public PartyDescriptor getMonsterPartyDescriptor() {
        return monsterPartyDescriptorRef.get();
    }

    public void export(BattleDirectorBuilder builder, ResourceContext resourceContext) {
        builder.bluePartyHpVisible(hpVisible);
        getMonsterPartyDescriptor().export(builder.blueParty(), resourceContext);
    }
}
