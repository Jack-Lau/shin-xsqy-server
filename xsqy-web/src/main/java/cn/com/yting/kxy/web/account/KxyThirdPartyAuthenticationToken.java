/*
 * Created 2018-10-15 11:51:08
 */
package cn.com.yting.kxy.web.account;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 *
 * @author Azige
 */
public class KxyThirdPartyAuthenticationToken extends AbstractAuthenticationToken {

    private Account account;

    public KxyThirdPartyAuthenticationToken(Account account) {
        super(Account.AUTHORITIES);
        this.account = account;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Account getPrincipal() {
        return account;
    }
}
