/*
 * Created 2018-11-2 15:57:26
 */
package cn.com.yting.kxy.web.awardpool;

import java.util.Map;

import cn.com.yting.kxy.core.resetting.ResetType;
import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Azige
 */
public final class AwardPoolConstants {

    public static final long POOL_ID_TREASURE = 1;

    public static final Map<Long, AwardPoolConfig> CONFIG_MAP = ImmutableMap.<Long, AwardPoolConfig>builder()
        .put(POOL_ID_TREASURE, new AwardPoolConfig(10_000_000, ResetType.DAILY, 10_000_000, ResetType.DAILY))
        .build();

    private AwardPoolConstants() {

    }
}
