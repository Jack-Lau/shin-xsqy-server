/*
 * Created 2018-6-25 11:20:39
 */
package cn.com.yting.kxy.web.config;

import java.util.LinkedHashMap;
import java.util.UUID;

import cn.com.yting.kxy.web.account.AccountUserDetailsService;
import cn.com.yting.kxy.web.account.KxyAuthenticationProvider;
import cn.com.yting.kxy.web.account.PathTokenRememberMeServices;
import cn.com.yting.kxy.web.apple.AppleLoginFilter;
import cn.com.yting.kxy.web.ethereum.EthereumLoginFilter;
import cn.com.yting.kxy.web.taptap.TaptapLoginFilter;
import cn.com.yting.kxy.web.account.weixin.WeixinLoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.ForwardAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.ForwardLogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 *
 * @author Azige
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String rememberMeKey = UUID.randomUUID().toString();

    @Value("${kxy.web.debug}")
    private boolean debugEnabled;

    @Autowired
    private AccountUserDetailsService accountUserDetailsService;
    @Autowired
    private KxyAuthenticationProvider kxyThirdPartyAuthenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(kxyThirdPartyAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter("UTF-8");
        if (debugEnabled) {
            http
                .authorizeRequests()
                // 管理相关的接口，在调试模式下允许任何人使用
                .antMatchers("/management/**").permitAll();
        } else {
            http
                .authorizeRequests()
                // 管理相关的接口，在非调试模式下只允许从本地访问
                .antMatchers("/management/**").hasIpAddress("127.0.0.1")
                // 非调试模式下禁止使用用户名密码注册和登录
                .antMatchers("/account/register/createTest", "/account/login").denyAll();
        }
        http
            .addFilterBefore(encodingFilter, CsrfFilter.class)
            .addFilterAt(weixinLoginFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(taptapLoginFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(appleLoginFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(ethereumLoginFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            // 未登录的用户注册账号相关的接口
            .antMatchers("/account/register/*").permitAll()
            // 非登录用户也可以查看的常规信息
            .antMatchers("/", "/static/**", "/kxyEquipment/**", "/kxyPet/**").permitAll()
            // 用于第三方平台的开放接口
            .antMatchers("/public/**").permitAll()
            // 调试和文档用接口，确保在正式部署时 DebugController ApiController 都是不启用的状态
            .antMatchers("/debug/**", "/api/**").permitAll()
            .anyRequest().authenticated()
            .and()
            //
            .formLogin()
            .loginProcessingUrl("/account/login")
            .successHandler(new ForwardAuthenticationSuccessHandler("/account/view/myself"))
            .failureHandler(authenticationFailureHandler())
            .permitAll()
            .and()
            //
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/account/logout"))
            .logoutSuccessHandler(new ForwardLogoutSuccessHandler("/"))
            .permitAll()
            .and()
            //
            .rememberMe()
            .rememberMeServices(rememberMeServices())
            .key(rememberMeKey)
            .and()
            //
            .csrf()
            .disable()
            //
            .cors()
            .and()
            //
            .exceptionHandling()
            .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
            .and()
            //
            .headers()
            .frameOptions().disable()
            .and();
    }

    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        PathTokenRememberMeServices bean = new PathTokenRememberMeServices(rememberMeKey, accountUserDetailsService);
        bean.setAlwaysRemember(true);
        return bean;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        LinkedHashMap<Class<? extends AuthenticationException>, AuthenticationFailureHandler> map = new LinkedHashMap<>();
        SimpleUrlAuthenticationFailureHandler forwardHandler = new SimpleUrlAuthenticationFailureHandler("/accountLocked");
        forwardHandler.setUseForward(true);
        map.put(LockedException.class, forwardHandler);
        return new DelegatingAuthenticationFailureHandler(map, new SimpleUrlAuthenticationFailureHandler());
    }

    @Bean
    public WeixinLoginFilter weixinLoginFilter() throws Exception {
        WeixinLoginFilter bean = new WeixinLoginFilter();
        bean.setAuthenticationManager(authenticationManager());
        bean.setRememberMeServices(rememberMeServices());
        bean.setAuthenticationSuccessHandler(new ForwardAuthenticationSuccessHandler("/account/login-weixin-success"));
        bean.setAuthenticationFailureHandler(authenticationFailureHandler());
        return bean;
    }

    @Bean
    public TaptapLoginFilter taptapLoginFilter() throws Exception {
        TaptapLoginFilter bean = new TaptapLoginFilter();
        bean.setAuthenticationManager(authenticationManager());
        bean.setRememberMeServices(rememberMeServices());
        bean.setAuthenticationSuccessHandler(new ForwardAuthenticationSuccessHandler("/account/login-weixin-success"));
        bean.setAuthenticationFailureHandler(authenticationFailureHandler());
        return bean;
    }

    @Bean
    public AppleLoginFilter appleLoginFilter() throws Exception {
        AppleLoginFilter bean = new AppleLoginFilter();
        bean.setAuthenticationManager(authenticationManager());
        bean.setRememberMeServices(rememberMeServices());
        bean.setAuthenticationSuccessHandler(new ForwardAuthenticationSuccessHandler("/account/login-weixin-success"));
        bean.setAuthenticationFailureHandler(authenticationFailureHandler());
        return bean;
    }

    @Bean
    public EthereumLoginFilter ethereumLoginFilter() throws Exception {
        EthereumLoginFilter bean = new EthereumLoginFilter();
        bean.setAuthenticationManager(authenticationManager());
        bean.setRememberMeServices(rememberMeServices());
        bean.setAuthenticationSuccessHandler(new ForwardAuthenticationSuccessHandler("/account/login-weixin-success"));
        bean.setAuthenticationFailureHandler(authenticationFailureHandler());
        return bean;
    }
}
