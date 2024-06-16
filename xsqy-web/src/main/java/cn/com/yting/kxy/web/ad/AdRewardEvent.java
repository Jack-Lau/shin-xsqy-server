// Created 2021/8/25 16:17

package cn.com.yting.kxy.web.ad;

import lombok.Value;
import org.springframework.lang.Nullable;

/**
 * @author Azige
 */
@Value
public class AdRewardEvent {
    long accountId;
    String transactionId;
    @Nullable String extra;
}
