/*
 * Created 2016-1-8 16:07:58
 */
package cn.com.yting.kxy.battle.generate;

import java.util.Map;
import java.util.function.Supplier;

import javax.script.Bindings;

import cn.com.yting.kxy.battle.PartyBuilder;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.UnitBuilder;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;

/**
 *
 * @author Azige
 */
public class PartyDescriptor implements Resource {

    private long id;
    private Map<Integer, UnitEntry> unitDescriptorMap;

    @Override
    public long getId() {
        return id;
    }

    public static class UnitEntry {

        private final Supplier<UnitDescriptor> unitDescriptorSupplier;
        private final int playerCountLimit;

        public UnitEntry(Supplier<UnitDescriptor> unitDescriptorSupplier, int playerCountLimit) {
            this.unitDescriptorSupplier = unitDescriptorSupplier;
            this.playerCountLimit = playerCountLimit;
        }

        public Supplier<UnitDescriptor> getUnitDescriptorSupplier() {
            return unitDescriptorSupplier;
        }

        public int getPlayerCountLimit() {
            return playerCountLimit;
        }

    }

    public PartyDescriptor(long id, Map<Integer, UnitEntry> unitDescriptorMap) {
        this.id = id;
        this.unitDescriptorMap = unitDescriptorMap;
    }

    public void export(PartyBuilder<?> pb, ResourceContext resourceContext) {
        unitDescriptorMap.forEach((key, value) -> {
            UnitDescriptor unitDescriptor = value.getUnitDescriptorSupplier().get();
            if (unitDescriptor != null) {
                UnitBuilder<?> ub = pb.unit(key);
                unitDescriptor.export(ub, resourceContext);
                ub.stance(Unit.Stance.STANCE_BLUE);
                ub.type(Unit.UnitType.TYPE_MONSTER);
            }
        });
    }
}
