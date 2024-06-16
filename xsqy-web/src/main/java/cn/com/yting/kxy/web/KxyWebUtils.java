/*
 * Created 2018-9-10 17:43:26
 */
package cn.com.yting.kxy.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import cn.com.yting.kxy.web.message.WebMessageWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Azige
 */
public final class KxyWebUtils {

    private static final Logger LOG = LoggerFactory.getLogger(KxyWebUtils.class);

    private static final String bannedResponseBody;

    static {
        String text = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            WebMessageWrapper message = WebMessageWrapper.error(KxyWebException.EC_CONCURRENCY_ERROR, "");
            text = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException ex) {
            LOG.error("生成被拦截的请求的响应文本失败", ex);
        }
        bannedResponseBody = text;
    }

    public static void writeToBannedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.getWriter().append(bannedResponseBody).close();
    }

    private KxyWebUtils() {
    }
}
