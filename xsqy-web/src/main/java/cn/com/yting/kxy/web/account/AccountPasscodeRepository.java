/*
 * Created 2018-8-21 12:43:12
 */
package cn.com.yting.kxy.web.account;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Azige
 */
public interface AccountPasscodeRepository extends JpaRepository<AccountPasscode, AccountPasscode.PK> {

    AccountPasscode findByPasscodeTypeAndPasscode(AccountPasscodeType passcodeType, String passcode);
}
