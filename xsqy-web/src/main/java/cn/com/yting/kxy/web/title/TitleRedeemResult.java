/*
 * Created 2018-11-8 16:29:09
 */
package cn.com.yting.kxy.web.title;

import cn.com.yting.kxy.web.award.AwardResult;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class TitleRedeemResult {

    private Title title;
    private AwardResult awardResult;
}
