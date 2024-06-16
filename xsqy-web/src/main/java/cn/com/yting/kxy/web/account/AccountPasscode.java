/*
 * Created 2018-8-20 12:18:44
 */
package cn.com.yting.kxy.web.account;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Azige
 */
@Entity
@Data
@Table(name = "account_passcode", indexes = @Index(columnList = "passcode_type, passcode"))
@IdClass(AccountPasscode.PK.class)
public class AccountPasscode implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Id
    @Column(name = "passcode_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private AccountPasscodeType passcodeType;
    @Column(name = "passcode", nullable = false)
    @JsonIgnore
    private String passcode;

    @ManyToOne(optional = false)
    @MapsId("account_id")
    @JsonIgnore
    private Account account;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {

        private long accountId;
        private AccountPasscodeType passcodeType;

    }
}
