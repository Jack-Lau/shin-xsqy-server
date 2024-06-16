/*
 * Created 2015-10-13 11:18:29
 */
package cn.com.yting.kxy.battle;

import cn.com.yting.kxy.battle.BattleConstant.FURY_MODEL;
import cn.com.yting.kxy.battle.handlers.ActionBeforeHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import cn.com.yting.kxy.battle.handlers.AllDeadBattleEndHandler;
import cn.com.yting.kxy.battle.handlers.ActionEndHandler;
import cn.com.yting.kxy.battle.handlers.DamageActivateHandler;
import cn.com.yting.kxy.battle.handlers.ReselectTargetHandler;
import cn.com.yting.kxy.battle.handlers.SpeedRandomizeHandler;
import cn.com.yting.kxy.battle.handlers.TurnEndHandler;
import cn.com.yting.kxy.battle.handlers.TurnLimitedBattleEndHandler;
import io.github.azige.mgxy.event.EventDispatcher;
import io.github.azige.mgxy.event.EventHandler;

/**
 *
 * @author Azige
 */
public class BattleDirectorBuilder {

    private final BattlePartyBuilder redPartyBuilder = new BattlePartyBuilder();
    private final BattlePartyBuilder bluePartyBuilder = new BattlePartyBuilder();

    private int turnLimit = 60;
    private FURY_MODEL furyModel = FURY_MODEL.NONE;
    private boolean redPartyHpVisible = true;
    private boolean bluePartyHpVisible = true;
    private final List<EventHandler<?>> eventHandlers = new ArrayList<>();

    public class BattlePartyBuilder extends PartyBuilder<BattlePartyBuilder> {

        public class BattleUnitBuilder extends UnitBuilder<BattleUnitBuilder> {

            private BattleUnitBuilder() {

            }

            public BattlePartyBuilder andParty() {
                return BattlePartyBuilder.this;
            }
        }

        private BattlePartyBuilder() {
        }

        @Override
        public BattleUnitBuilder unit(int position) {
            BattleUnitBuilder ub = new BattleUnitBuilder();
            unit(position, ub);
            return ub;
        }

        public BattleDirectorBuilder andBattle() {
            return BattleDirectorBuilder.this;
        }
    }

    public BattlePartyBuilder redParty() {
        return redPartyBuilder;
    }

    public BattlePartyBuilder blueParty() {
        return bluePartyBuilder;
    }

    public BattleDirectorBuilder turnLimit(int value) {
        turnLimit = value;
        return this;
    }

    public BattleDirectorBuilder furyModel(FURY_MODEL value) {
        furyModel = value;
        return this;
    }

    public BattleDirectorBuilder redPartyHpVisible(boolean value) {
        redPartyHpVisible = value;
        return this;
    }

    public BattleDirectorBuilder bluePartyHpVisible(boolean value) {
        bluePartyHpVisible = value;
        return this;
    }

    public BattleDirectorBuilder handler(EventHandler<?> handler) {
        eventHandlers.add(handler);
        return this;
    }

    public BattleDirector build() {
        if (redPartyBuilder == null || bluePartyBuilder == null) {
            throw new IllegalStateException();
        }
        //
        Party redParty = redPartyBuilder.build();
        redParty.getUnitMap().values().stream().map((unit) -> {
            unit.setHpVisible(redPartyHpVisible);
            return unit;
        }).forEachOrdered((unit) -> {
            unit.getBattlePetUnitQueue().forEach((petUnit) -> {
                petUnit.setHpVisible(redPartyHpVisible);
            });
        });
        //
        Party blueParty = bluePartyBuilder.build();
        blueParty.getUnitMap().values().stream().map((unit) -> {
            unit.setHpVisible(bluePartyHpVisible);
            return unit;
        }).forEachOrdered((unit) -> {
            unit.getBattlePetUnitQueue().forEach((petUnit) -> {
                petUnit.setHpVisible(bluePartyHpVisible);
            });
        });
        //
        BattleDirector bd = new BattleDirector(redPartyBuilder.build(), blueParty);
        EventDispatcher ed = bd.getEventDispatcher();
        Stream.concat(eventHandlers.stream(), createDefaultHandlers().stream())
                .forEach(ed::addHandler);
        bd.setFuryModel(furyModel);
        return bd;
    }

    private List<EventHandler<?>> createDefaultHandlers() {
        return Arrays.asList(new SpeedRandomizeHandler(),
                new TurnLimitedBattleEndHandler(turnLimit),
                new AllDeadBattleEndHandler(),
                new ReselectTargetHandler(),
                new DamageActivateHandler(),
                new ActionBeforeHandler(),
                new ActionEndHandler(),
                new TurnEndHandler()
        );
    }

}
