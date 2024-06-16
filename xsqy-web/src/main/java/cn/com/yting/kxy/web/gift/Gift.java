/*
 * Created 2017-6-23 12:56:03
 */
package cn.com.yting.kxy.web.gift;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "gift", indexes = @Index(columnList = "gift_definition_id, redeemer_account_id"))
@Data
public class Gift implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "gift_definition_id", nullable = false)
    private long giftDefinitionId;
    @Column(name = "code", nullable = false, unique = true)
    private String code;
    @Column(name = "redeemed", nullable = false)
    private boolean redeemed = false;
    @Column(name = "redeemer_account_id")
    private Long redeemerAccountId;
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Column(name = "redeem_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date redeemTime;
}
