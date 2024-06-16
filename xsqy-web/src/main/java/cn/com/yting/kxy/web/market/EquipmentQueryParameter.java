/*
 * Created 2018-12-26 13:03:04
 */
package cn.com.yting.kxy.web.market;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class EquipmentQueryParameter {

    private Integer part;
    private Integer color;
    private Integer maxEnhanceLevel;
    private String paramMatch;
    private Integer patk;
    private Integer matk;
    private Long fc;
    private String effectMatch;
    private String effectIdsText;
    private String skillEnhancementEffectIds;
}
