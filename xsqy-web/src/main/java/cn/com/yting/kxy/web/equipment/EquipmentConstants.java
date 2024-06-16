/*
 * Created 2018-9-18 18:20:49
 */
package cn.com.yting.kxy.web.equipment;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;

import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiUnits;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 *
 * @author Azige
 */
public class EquipmentConstants {

    public static final int PART_TYPE_HAND = 10;
    public static final int PART_TYPE_BODY = 22;
    public static final int PART_TYPE_WAIST = 24;
    public static final int PART_TYPE_FOOT = 23;
    public static final int PART_TYPE_HEAD = 21;
    public static final int PART_TYPE_NECK = 25;

    public static final Map<String, Double> PARAMETER_NAME_TO_FC_FACTOR = ImmutableMap.<String, Double>builder()
            .put(ParameterNameConstants.最大生命, 0.135)
            .put(ParameterNameConstants.物伤, 0.5)
            .put(ParameterNameConstants.法伤, 0.5)
            .put(ParameterNameConstants.物防, 1.255)
            .put(ParameterNameConstants.法防, 1.255)
            .put(ParameterNameConstants.速度, 3.0)
            .put(ParameterNameConstants.幸运, 3.18)
            .build();

    public static final Collection<Long> DEFINITION_IDS_BINGO = ImmutableSet.of(10005L, 21005L, 22005L, 23005L, 24005L, 25005L);

    public static final long BROADCAST_ID_FORGE_BINGO = 3200008;
    public static final long BROADCAST_ID_ENHANCE_LV7 = 3200013;
    public static final long BROADCAST_ID_ENHANCE_LV10 = 3200014;
    public static final long BROADCAST_ID_SOUL_LEVEL_OVER_10 = 3200069;
    public static final long BROADCAST_ID_SOUL_NAME_BINGO = 3200070;
    public static final long BROADCAST_ID_SOUL_NAME_ID_BINGO = 3200071;

    public static final int FORGE_REQUIRED_PLAYER_LEVEL = 1;

    public static final Duration DURATION_NEXT_WITHDRAW_TIME_FROM_ETHEREUM = Duration.ofDays(14);
    public static final Duration DURATION_NEXT_WITHDRAW_TIME_FROM_MARKET = Duration.ofDays(14);
    public static final Duration DURATION_NEXT_WITHDRAW_TIME_FROM_REDEEM = Duration.ofDays(3);

    public static final int COUNT_MAX_EFFECTS = 6;

    public static final long RECYCLE_BLUE_EQUIPMENT_GET_195 = 1000;
    public static final long RECYCLE_PURPLE_EQUIPMENT_GET_195 = 50000;

    public static final long PRICE_FORGE = KuaibiUnits.fromKuaibi(50);
    public static final long PRICE_FUSION = 10000;
    public static final long PRICE_WASH = 10000;
    
    public static final long fc(Equipment equipment, ResourceContext resourceContext) {
        return (long) equipment.createParameterSpace(resourceContext)
                .asRootParameterSpace()
                .getParameter(ParameterNameConstants.战斗力)
                .getValue();
    }

}
