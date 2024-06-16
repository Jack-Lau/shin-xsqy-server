/*
 * Created 2018-11-2 16:26:09
 */
package cn.com.yting.kxy.web.awardpool;

import cn.com.yting.kxy.core.resetting.ResetType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class AwardPoolConfig {

    private long initPublicPoolValue;
    private ResetType publicPoolResetType;
    private long initPersonalPoolValue;
    private ResetType personalPoolResetType;
}
