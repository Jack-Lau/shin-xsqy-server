/*
 * Created 2018-11-5 11:11:24
 */
package cn.com.yting.kxy.web.track;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/track")
public class ClientErrorTrackController implements ModuleApiProvider {

    @Autowired
    private ClientErrorLogger clientErrorLogger;

    private final Cache<Long, AtomicInteger> accessFrequencyCache = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofHours(1))
        .build();

    @PostMapping("/reportMessage")
    public WebMessageWrapper reportMessage(
        @AuthenticationPrincipal Account account,
        @RequestParam("message") String message
    ) {
        try {
            AtomicInteger accessFrequency = accessFrequencyCache.get(account.getId(), AtomicInteger::new);
            if (accessFrequency.get() < 20) {
                accessFrequency.getAndIncrement();
                clientErrorLogger.logMessage(account.getId(), message);
            }
        } catch (ExecutionException ex) {
            // Impossible
        }
        return WebMessageWrapper.ok();
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .name("track")
            .baseUri("/track")
            //
            .webInterface()
            .uri("/reportMessage")
            .post()
            .description("提交一个错误信息以记录")
            .requestParameter("string", "message", "需要记录的消息")
            .and();
    }
}
