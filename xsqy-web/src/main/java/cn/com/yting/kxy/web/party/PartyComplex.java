/*
 * Created 2018-9-27 18:47:45
 */
package cn.com.yting.kxy.web.party;

import java.util.List;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class PartyComplex {

    private PartyRecord partyRecord;
    private List<SupportRelation> supportRelations;
    private int supporterForOthersCount;

}
