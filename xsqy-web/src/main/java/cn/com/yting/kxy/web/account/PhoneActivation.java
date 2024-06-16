/*
 * Created 2018-6-30 18:00:37
 */
package cn.com.yting.kxy.web.account;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "phone_activation")
@Data
public class PhoneActivation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    @Column(name = "activation_code", nullable = false, unique = true)
    private int activationCode;
    @Column(name = "creation_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime = new Date();
}
