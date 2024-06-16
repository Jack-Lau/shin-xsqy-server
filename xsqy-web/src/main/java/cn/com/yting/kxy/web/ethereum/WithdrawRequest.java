/*
 * Created 2018-8-27 19:06:12
 */
package cn.com.yting.kxy.web.ethereum;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "withdraw_request", indexes = {
    @Index(columnList = "request_status"),
    @Index(columnList = "account_id, request_status")
})
@Data
@WebMessageType
public class WithdrawRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "account_id", nullable = false)
    private long accountId;
    @Column(name = "transaction_hash", nullable = false)
    private String transactionHash;
    @Column(name = "amount", nullable = false)
    private long amount;
    @Column(name = "fee", nullable = false)
    private long fee;
    @Column(name = "request_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private WithdrawRequestStatus requestStatus = WithdrawRequestStatus.PENDING;
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
}
