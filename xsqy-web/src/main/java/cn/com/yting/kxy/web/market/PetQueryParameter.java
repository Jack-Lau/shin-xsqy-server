/*
 * Created 2018-12-26 19:23:57
 */
package cn.com.yting.kxy.web.market;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class PetQueryParameter {

    private Long petDefinitionId;
    private Integer petRank;
    private Integer maxPetRank;
    private Integer aptitudeHp;
    private Integer aptitudeAtk;
    private Integer aptitudePdef;
    private Integer aptitudeMdef;
    private Integer aptitudeSpd;
    private String abilityMatch;
    private String abilityIdsText;
}
