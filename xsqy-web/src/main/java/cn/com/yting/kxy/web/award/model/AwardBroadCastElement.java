/*
 * Created 2018-8-3 16:43:04
 */
package cn.com.yting.kxy.web.award.model;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class AwardBroadCastElement implements AwardElement {

    private long broadcastId;

    @Override
    public void apply(AwardBuilder builder, int playerLevel, long playerFc) {
        builder.addBroadcast(broadcastId);
    }
}
