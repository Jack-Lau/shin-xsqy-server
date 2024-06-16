/*
 * Created 2018-6-25 10:45:21
 */
package cn.com.yting.kxy.web.controller;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.account.whitelist.WhiteListStatusHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
public class HelloController {

    @Value("${git.commit.id}")
    private String commitId;
    @Value("${git.build.version}")
    private String buildVersion;
    @Value("${git.build.time}")
    private String buildTime;

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private WhiteListStatusHolder whiteListStatusHolder;

    @RequestMapping("/")
    public Object hello(
            @AuthenticationPrincipal Account account
    ) {
        return new Info(
                timeProvider.currentTime(),
                timeProvider.currentOffsetDateTime().toString(),
                commitId,
                buildVersion,
                buildTime,
                whiteListStatusHolder.isEnabled(),
                "v1.0.0",
                1
        );
    }

    @lombok.Value
    public static class Info {

        private long currentTime;
        private String readableCurrentTime;
        private String commitId;
        private String buildVersion;
        private String buildTime;
        private boolean restrictedMode;
        private String clientVersion;
        private long serialNumber;

    }

}
