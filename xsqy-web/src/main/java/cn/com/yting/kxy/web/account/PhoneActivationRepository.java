/*
 * Created 2018-6-30 18:09:16
 */
package cn.com.yting.kxy.web.account;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface PhoneActivationRepository extends JpaRepository<PhoneActivation, Long> {

    PhoneActivation findByPhoneNumber(String phoneNumber);

    PhoneActivation findByActivationCode(int activationCode);

    boolean existsByActivationCode(int activationCode);

    @Modifying
    @Query("DELETE FROM PhoneActivation pa WHERE pa.creationTime < ?1")
    void deleteExpired(Date expiringTime);
}
