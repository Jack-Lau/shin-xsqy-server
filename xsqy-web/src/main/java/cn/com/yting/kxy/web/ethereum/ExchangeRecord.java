/*
 * Created 2018-12-3 15:50:13
 */
package cn.com.yting.kxy.web.ethereum;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "exchange_record")
@Data
public class ExchangeRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "last_kuaibi_withdraw_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastKuaibiWithdrawTime;
    @Column(name = "kuaibi_withdraw_count", nullable = false)
    private long kuaibiWithdrawCount;
    @Column(name = "last_kuaibi_deposit_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastKuaibiDepositTime;
    @Column(name = "kuaibi_deposit_count", nullable = false)
    private long kuaibiDepositCount;

    public void increaseKuaibiWithdrawCount(long value) {
        kuaibiWithdrawCount += value;
    }
}
