/*
 * Created 2018-7-30 16:45:52
 */
package cn.com.yting.kxy.web.chat.model;

import java.util.Map;

import cn.com.yting.kxy.web.message.WebMessageType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class TemplateDescription {

    private long id;
    private Map<String, String> args;

    @JsonCreator
    public TemplateDescription(
        @JsonProperty("id") long id,
        @JsonProperty("args") Map<String, String> args
    ) {
        this.id = id;
        this.args = args;
    }
}
