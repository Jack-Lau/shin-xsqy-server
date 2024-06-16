/*
 * Created 2018-6-29 11:25:51
 */
package cn.com.yting.kxy.web.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class WebMessageWrapper {

    public static final int STATUS_OK = 0;

    private final int status;
    private final String contentType;
    private final Object content;
    @JsonInclude(Include.NON_NULL)
    private final Object error;

    public static WebMessageWrapper ok() {
        return new WebMessageWrapper(STATUS_OK, "null", null, null);
    }

    public static WebMessageWrapper ok(String contentType, Object content) {
        return new WebMessageWrapper(STATUS_OK, contentType, content, null);
    }

    public static WebMessageWrapper error(int status, Object error) {
        return new WebMessageWrapper(status, "null", null, error);
    }
}
