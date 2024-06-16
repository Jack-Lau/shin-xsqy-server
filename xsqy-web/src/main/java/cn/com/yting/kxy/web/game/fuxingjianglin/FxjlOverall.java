/*
 * Created 2019-1-22 12:44:33
 */
package cn.com.yting.kxy.web.game.fuxingjianglin;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class FxjlOverall {

    private FxjlSharedRecord fxjlSharedRecord;
    private FxjlRecord fxjlRecord;
}
