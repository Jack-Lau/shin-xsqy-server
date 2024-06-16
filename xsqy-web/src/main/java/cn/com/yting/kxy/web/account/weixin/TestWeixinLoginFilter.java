/*
 * Created 2018-7-14 17:36:34
 */
package cn.com.yting.kxy.web.account.weixin;

import java.io.IOException;

/**
 * 测试时使用的微信登录过滤器，不使用微信 API，直接将 code 当作 union id
 *
 * @author Azige
 */
public class TestWeixinLoginFilter extends WeixinLoginFilter {

    @Override
    protected UserInfoResponse resolveUserInfo(String from, String code) throws IOException {
        UserInfoResponse userInfo = new UserInfoResponse();
        userInfo.setUnionid(code);
        userInfo.setNickname(code);
        return userInfo;
    }
}
