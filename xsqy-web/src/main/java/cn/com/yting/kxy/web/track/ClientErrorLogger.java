/*
 * Created 2018-11-5 11:09:18
 */
package cn.com.yting.kxy.web.track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
public class ClientErrorLogger {

    private static final Logger LOG = LoggerFactory.getLogger(ClientErrorLogger.class);

    public void logMessage(long accountId, String message) {
        LOG.info("account={}，{}", accountId, message);
    }
}
