// Created 2021/8/24 16:37

package cn.com.yting.kxy.web.ethereum;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.account.AccountService;
import cn.com.yting.kxy.web.account.KxyThirdPartyAuthenticationToken;
import cn.com.yting.kxy.web.account.weixin.WeixinLoginResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.util.NestedServletException;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Azige
 */
public class EthereumLoginFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(EthereumLoginFilter.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private TimeProvider timeProvider;

    public EthereumLoginFilter() {
        super(new AntPathRequestMatcher("/account/login-ethereum", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try {
            String time = ServletRequestUtils.getRequiredStringParameter(request, "time");
            String address = ServletRequestUtils.getRequiredStringParameter(request, "address");
            String signature = ServletRequestUtils.getRequiredStringParameter(request, "signature");
            try {
                if (Duration.ofMillis(Math.abs(timeProvider.currentTime() - Long.parseLong(time))).compareTo(Duration.ofMinutes(30)) > 0) {
                    throw new BadCredentialsException("时间错误：超过容许期限");
                }
            } catch (NumberFormatException e) {
                throw new BadCredentialsException("时间错误：格式不正确");
            }
            byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
            if (signatureBytes.length != 65) {
                throw new BadCredentialsException("签名错误：长度不正确");
            }
            byte v = signatureBytes[64];
            if (v < 27) {
                v += 27;
            }
            Sign.SignatureData sd = new Sign.SignatureData(
                v,
                Arrays.copyOfRange(signatureBytes, 0, 32),
                Arrays.copyOfRange(signatureBytes, 32, 64)
            );
            try {
                String recoveredAddress = Numeric.prependHexPrefix(Keys.getAddress(Sign.signedPrefixedMessageToKey(("XSQY:" + time).getBytes(), sd)));
                if (!recoveredAddress.equalsIgnoreCase(address)) {
                    throw new BadCredentialsException("签名错误：无法验证地址");
                }
            } catch (SignatureException e) {
                LOG.debug("", e);
                throw new BadCredentialsException("签名错误：无法解析");
            }

            AtomicBoolean newAccount = new AtomicBoolean(false);
            Account account = accountService.findOrCreateByEthereumAddress(address, newAccount::set);
            request.setAttribute("accountId", account.getId());
            request.setAttribute("result", new WeixinLoginResult(newAccount.get()));
            return getAuthenticationManager().authenticate(new KxyThirdPartyAuthenticationToken(account));
        } catch (ServletException ex) {
            throw new NestedServletException(ex.getMessage(), ex);
        } catch (AuthenticationException e) {
            LOG.debug("", e);
            throw e;
        }
    }
}
