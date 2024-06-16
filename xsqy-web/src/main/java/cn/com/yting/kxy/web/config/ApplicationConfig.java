/*
 * Created 2018-6-27 15:12:41
 */
package cn.com.yting.kxy.web.config;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.TransactionalTaskExecutor;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resetting.ResettingManager;
import cn.com.yting.kxy.core.resource.AutoScanResourceContext;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.scheduling.ScheduledTaskManager;
import cn.com.yting.kxy.core.wordfilter.ForbiddenWordsChecker;
import cn.com.yting.kxy.web.captcha.TencentCaptchaApi;
import cn.com.yting.kxy.web.filters.AccessFrequencyFilter;
import cn.com.yting.kxy.web.player.PlayerExistenceFilter;
import cn.com.yting.kxy.web.filters.RequestRejectedFilter;
import cn.com.yting.kxy.web.util.AnnotatedTransactionalTaskExecutor;
import cn.com.yting.kxy.web.account.weixin.WeixinApi;
import cn.com.yting.kxy.web.captcha.CaptchaFilter;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.proof.ClientProoferFilter;
import cn.com.yting.kxy.web.scheduling.ScheduledTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Azige
 */
@Configuration
@EnableAsync
@EnableAspectJAutoProxy
@EnableScheduling
public class ApplicationConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfig.class);

    @Autowired
    @Lazy
    private ScheduledTaskService scheduledTaskService;
    @Autowired
    @Lazy
    private List<ResetTask> resetTasks;

    @Value("${kxy.web.scheduledTaskManager.enable}")
    private boolean scheduledTaskManagerEnabled;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        return taskScheduler;
    }

    @Bean
    public TimeProvider timeProvider() {
        return new TimeProvider();
    }

    @Bean
    public TransactionalTaskExecutor transactionalTaskExecutor() {
        return new AnnotatedTransactionalTaskExecutor();
    }

    @Bean
    @Lazy
    public ScheduledTaskManager scheduledTaskManager() {
        ScheduledTaskManager bean = new ScheduledTaskManager(taskScheduler(), scheduledTaskService, transactionalTaskExecutor(), timeProvider());
        bean.setEnabled(scheduledTaskManagerEnabled);
        return bean;
    }

    @Bean
    @Lazy
    public ResettingManager resettingManager() {
        return new ResettingManager(scheduledTaskManager(), resetTasks, transactionalTaskExecutor());
    }

    @Bean
    public ResourceContext resourceContext(@Value("${kxy.web.resource.location}") String resourceLocation) throws Exception {
        if (resourceLocation != null && !resourceLocation.isEmpty()) {
            return new AutoScanResourceContext(new URLClassLoader(new URL[]{new File(resourceLocation).toURI().toURL()}));
        } else {
            return new AutoScanResourceContext();
        }
    }

    @Bean
    public ForbiddenWordsChecker forbiddenWordsChecker() {
        try {
            return new ForbiddenWordsChecker();
        } catch (Exception ex) {
            LOG.warn("构造屏蔽词检查器时出错，将使用空屏蔽词列表", ex);
            return new ForbiddenWordsChecker(Collections.emptyList());
        }
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WeixinApi weixinApiAsWeb(
        @Value("${kxy.web.weixin.appId}") String appId,
        @Value("${kxy.web.weixin.secretKey}") String secretKey
    ) {
        return new WeixinApi(appId, secretKey);
    }

    @Bean
    public WeixinApi weixinApiAsPublic(
        @Value("${kxy.web.weixinPublic.appId}") String appId,
        @Value("${kxy.web.weixinPublic.secretKey}") String secretKey
    ) {
        return new WeixinApi(appId, secretKey);
    }

    @Bean
    public FilterRegistrationBean<?> globalAccessFrequencyFilter() {
        FilterRegistrationBean<?> bean = new FilterRegistrationBean<>(new AccessFrequencyFilter(1000));
        bean.setName("globalAccessFrequencyFilter");
        bean.addUrlPatterns("/*");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<?> requestRejectedFilterRegistrationBean() {
        FilterRegistrationBean<?> bean = new FilterRegistrationBean<>(new RequestRejectedFilter());
        bean.setName("requestRejectedFilter");
        bean.addUrlPatterns("/*");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        return bean;
    }

    @Bean
    @ConditionalOnProperty("kxy.web.captcha.enabled")
    public FilterRegistrationBean<?> captchaFilterRegistrationBean(
        @Value("${kxy.web.debug}") boolean debugEnabled,
        @Autowired TencentCaptchaApi captchaApi
    ) {
        FilterRegistrationBean<?> bean = new FilterRegistrationBean<>(new CaptchaFilter(debugEnabled, captchaApi));
        bean.setName("captchaFilter");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<?> playerExistenceFilter(
        @Autowired PlayerRepository playerRepository
    ) {
        FilterRegistrationBean<?> bean = new FilterRegistrationBean<>(new PlayerExistenceFilter(playerRepository));
        bean.setName("playerExistenceFilter");
        bean.addUrlPatterns("/*");
        bean.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<?> clientProoferFilter() {
        FilterRegistrationBean<?> bean = new FilterRegistrationBean<>(new ClientProoferFilter());
        bean.setName("clientProoferFilter");
        bean.addUrlPatterns(
            "/account/register/requestPhoneActivation",
            "/kbdzp/recoverEnergy",
            "/kbdzp/makeTurn",
            "/quest/action/*",
            "/battle/start",
            "/battle/action/*"
        );
        bean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return bean;
    }
}
