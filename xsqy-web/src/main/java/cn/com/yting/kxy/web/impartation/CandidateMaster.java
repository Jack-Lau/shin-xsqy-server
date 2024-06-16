/*
 * Created 2018-11-24 17:47:15
 */
package cn.com.yting.kxy.web.impartation;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class CandidateMaster {

    private long accountId;
    private long discipleCount;
}
