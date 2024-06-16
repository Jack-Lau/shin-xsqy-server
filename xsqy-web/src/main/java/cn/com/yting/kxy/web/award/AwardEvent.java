/*
 * Created 2018-8-11 17:37:21
 */
package cn.com.yting.kxy.web.award;

import cn.com.yting.kxy.web.KxyWebEvent;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class AwardEvent extends KxyWebEvent {

    private final long accountId;
    private final AwardResult result;

    public AwardEvent(Object source, long accountId, AwardResult result) {
        super(source);
        this.accountId = accountId;
        this.result = result;
    }
}
