/*
 * Created 2018-6-25 15:49:09
 */
package cn.com.yting.kxy.web.account;

import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.sms.ShortMessageServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class AccountService implements ResetTask {

    public static final long ACTIVATION_CODE_CREATION_INTERVAL = 60 * 1000;
    public static final long ACTIVATION_CODE_TTL = 300 * 1000;
    public static final String WEIXIN_USERNAME_PREFIX = "weixin@";
    public static final String TAPTAP_USERNAME_PREFIX = "taptap@";
    public static final String APPLE_USERNAME_PREFIX = "apple@";
    public static final String ETHEREUM_USERNAME_PREFIX = "ethereum@";

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountPasscodeRepository accountPasscodeRepository;
    @Autowired
    private PhoneActivationRepository phoneActivationRepository;

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ShortMessageServiceApi shortMessageServiceApi;

    public Account registerForTest(String username, String password) {
        return createByPassword(username, password);
    }

    public Account registerByPhoneActivation(String phoneNumber, String password, int activationCode) {
        PhoneActivation phoneActivation = verifyAndGetPhoneActivation(phoneNumber, activationCode);

        Account account = createByPassword(phoneNumber, password);
        phoneActivationRepository.delete(phoneActivation);

        return account;
    }

    public Account findOrCreateByWeixinUnionId(String unionId, String nickName) {
        return findOrCreateByWeixinUnionId(unionId, nickName, a -> {});
    }

    public Account findOrCreateByWeixinUnionId(String unionId, String nickName, Consumer<Boolean> newAccountReceiver) {
        AccountPasscode passcode = accountPasscodeRepository.findByPasscodeTypeAndPasscode(AccountPasscodeType.WX_UNIONID, unionId);
        Account account;
        if (passcode == null) {
            String username = WEIXIN_USERNAME_PREFIX + unionId;
            account = new Account();
            account.setUsername(username);
            account.setDisplayName(nickName);
            account.setCreateTime(new Date(timeProvider.currentTime()));
            account = accountRepository.saveAndFlush(account);

            passcode = new AccountPasscode();
            passcode.setAccountId(account.getId());
            passcode.setPasscodeType(AccountPasscodeType.WX_UNIONID);
            passcode.setPasscode(unionId);
            passcode.setAccount(account);

            account.getPasscodes().add(passcode);

            eventPublisher.publishEvent(new AccountCreatedEvent(this, account));

            newAccountReceiver.accept(true);
        } else {
            account = passcode.getAccount();
        }

        return account;
    }

    public Account findOrCreateByTaptapUserId(String userId, String nickName, Consumer<Boolean> newAccountReceiver) {
        AccountPasscode passcode = accountPasscodeRepository.findByPasscodeTypeAndPasscode(AccountPasscodeType.TAPTAP_USER_ID, userId);
        Account account;
        if (passcode == null) {
            String username = TAPTAP_USERNAME_PREFIX + userId;
            account = new Account();
            account.setUsername(username);
            account.setDisplayName(nickName);
            account.setCreateTime(new Date(timeProvider.currentTime()));
            account = accountRepository.saveAndFlush(account);

            passcode = new AccountPasscode();
            passcode.setAccountId(account.getId());
            passcode.setPasscodeType(AccountPasscodeType.TAPTAP_USER_ID);
            passcode.setPasscode(userId);
            passcode.setAccount(account);

            account.getPasscodes().add(passcode);

            eventPublisher.publishEvent(new AccountCreatedEvent(this, account));

            newAccountReceiver.accept(true);
        } else {
            account = passcode.getAccount();
        }

        return account;
    }

    public Account findOrCreateByAppleId(String appleId, Consumer<Boolean> newAccountReceiver) {
        AccountPasscode passcode = accountPasscodeRepository.findByPasscodeTypeAndPasscode(AccountPasscodeType.APPLE_ID, appleId);
        Account account;
        if (passcode == null) {
            String username = APPLE_USERNAME_PREFIX + appleId;
            String displayName = username;
            if (displayName.length() > 10) {
                displayName = displayName.substring(0, 10);
            }
            account = new Account();
            account.setUsername(username);
            account.setDisplayName(displayName);
            account.setCreateTime(new Date(timeProvider.currentTime()));
            account = accountRepository.saveAndFlush(account);

            passcode = new AccountPasscode();
            passcode.setAccountId(account.getId());
            passcode.setPasscodeType(AccountPasscodeType.APPLE_ID);
            passcode.setPasscode(appleId);
            passcode.setAccount(account);

            account.getPasscodes().add(passcode);

            eventPublisher.publishEvent(new AccountCreatedEvent(this, account));

            newAccountReceiver.accept(true);
        } else {
            account = passcode.getAccount();
        }

        return account;
    }

    public Account findOrCreateByEthereumAddress(String address, Consumer<Boolean> newAccountReceiver) {
        AccountPasscode passcode = accountPasscodeRepository.findByPasscodeTypeAndPasscode(AccountPasscodeType.ETHEREUM_ADDRESS, address);
        Account account;
        if (passcode == null) {
            String username = ETHEREUM_USERNAME_PREFIX + address;
            String displayName = address;
            if (displayName.length() > 10) {
                displayName = displayName.substring(0, 10);
            }
            account = new Account();
            account.setUsername(username);
            account.setDisplayName(displayName);
            account.setCreateTime(new Date(timeProvider.currentTime()));
            account = accountRepository.saveAndFlush(account);

            passcode = new AccountPasscode();
            passcode.setAccountId(account.getId());
            passcode.setPasscodeType(AccountPasscodeType.ETHEREUM_ADDRESS);
            passcode.setPasscode(address);
            passcode.setAccount(account);

            account.getPasscodes().add(passcode);

            eventPublisher.publishEvent(new AccountCreatedEvent(this, account));

            newAccountReceiver.accept(true);
        } else {
            account = passcode.getAccount();
        }

        return account;
    }

    private Account createByPassword(String username, String password) {
        if (accountRepository.existsByUsername(username)) {
            throw AccountException.usernameExisted(username);
        }

        Account account = new Account();
        account.setUsername(username);
        account.setDisplayName(username);
        account.setCreateTime(new Date(timeProvider.currentTime()));
        account = accountRepository.saveAndFlush(account);

        AccountPasscode passcode = new AccountPasscode();
        passcode.setAccountId(account.getId());
        passcode.setPasscodeType(AccountPasscodeType.PASSWORD);
        passcode.setPasscode(passwordEncoder.encode(password));
        passcode.setAccount(account);

        account.getPasscodes().add(passcode);

        eventPublisher.publishEvent(new AccountCreatedEvent(this, account));

        return account;
    }

    public Account resetPassword(String phoneNumber, String password, int activationCode) {
        PhoneActivation phoneActivation = verifyAndGetPhoneActivation(phoneNumber, activationCode);

        Account account = accountRepository.findByUsername(phoneNumber);
        if (account == null) {
            throw KxyWebException.notFound("账号不存在：" + phoneNumber);
        }
        AccountPasscode passcode = account.getPasscodes().stream()
            .filter(it -> it.getPasscodeType().equals(AccountPasscodeType.PASSWORD))
            .findAny().orElseThrow(() -> AccountException.passcodeNotModifiable());

        passcode.setPasscode(passwordEncoder.encode(password));

        accountRepository.save(account);
        phoneActivationRepository.delete(phoneActivation);

        return account;
    }

    public Account addPassword(long accountId, String phoneNumber, String password, int activationCode) {
        PhoneActivation phoneActivation = verifyAndGetPhoneActivation(phoneNumber, activationCode);
        if (accountRepository.existsByUsername(phoneNumber)) {
            throw AccountException.usernameExisted2(phoneNumber);
        }

        Account account = accountRepository.findById(accountId).get();
        account.setUsername(phoneNumber);

        AccountPasscode passcode = new AccountPasscode();
        passcode.setAccountId(account.getId());
        passcode.setPasscodeType(AccountPasscodeType.PASSWORD);
        passcode.setPasscode(passwordEncoder.encode(password));
        passcode.setAccount(account);

        account.getPasscodes().add(passcode);

        accountRepository.save(account);
        phoneActivationRepository.delete(phoneActivation);

        return account;
    }

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("\\d{11}");
    private final Object createPhoneActivationLock = new Object();

    public PhoneActivation createPhoneActivation(String phoneNumber) {
        synchronized (createPhoneActivationLock) {
            if (!PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
                throw AccountException.illegalPhoneNumber();
            }
            PhoneActivation phoneActivation = phoneActivationRepository.findByPhoneNumber(phoneNumber);
            if (phoneActivation != null) {
                if (timeProvider.currentTime() - phoneActivation.getCreationTime().getTime() < ACTIVATION_CODE_CREATION_INTERVAL) {
                    throw AccountException.requestActivationCodeTooFast();
                }
                phoneActivationRepository.delete(phoneActivation);
            }

            phoneActivation = new PhoneActivation();
            phoneActivation.setPhoneNumber(phoneNumber);
            find_a_code:
            {
                for (int i = 0; i < 1000; i++) {
                    int code = generateActivationCode();
                    if (!phoneActivationRepository.existsByActivationCode(code)) {
                        phoneActivation.setActivationCode(code);
                        break find_a_code;
                    }
                }
                throw KxyWebException.unknown("无法在限定次数内找到一个有效的验证码");
            }
            phoneActivation = phoneActivationRepository.save(phoneActivation);
            shortMessageServiceApi.sendActivationCode(phoneNumber, phoneActivation.getActivationCode());

            return phoneActivation;
        }
    }

    public PhoneActivation verifyAndGetPhoneActivation(String phoneNumber, int activationCode) {
        PhoneActivation phoneActivation = phoneActivationRepository.findByActivationCode(activationCode);
        if (phoneActivation == null) {
            throw AccountException.activationCodeNotExist();
        }
        if (timeProvider.currentTime() - phoneActivation.getCreationTime().getTime() > ACTIVATION_CODE_TTL) {
            throw AccountException.activationCodeExpired();
        }
        if (!Objects.equals(phoneNumber, phoneActivation.getPhoneNumber())) {
            throw AccountException.activationCodeNotValid();
        }

        return phoneActivation;
    }

    private int generateActivationCode() {
        Random random = RandomProvider.getRandom();
        return 100_000 + random.nextInt(900_000);
    }

    @Override
    public void hourlyReset() {
        Date expiringTime = new Date(timeProvider.currentTime() - ACTIVATION_CODE_TTL);
        phoneActivationRepository.deleteExpired(expiringTime);
    }
}
