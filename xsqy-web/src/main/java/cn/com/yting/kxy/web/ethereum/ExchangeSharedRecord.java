/*
 * Created 2018-12-5 11:02:08
 */
package cn.com.yting.kxy.web.ethereum;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.com.yting.kxy.web.repository.LongId;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "exchange_shared_record")
@Data
public class ExchangeSharedRecord implements Serializable, LongId {

    @Id
    private Long id;
    @Column(name = "kuaibi_withdraw_count", nullable = false)
    private long kuaibiWithdrawCount;
    @Column(name = "kuaibi_deposit_count", nullable = false)
    private long kuaibiDepositCount;

    public void increaseKuaibiWithdrawCount(long value) {
        kuaibiWithdrawCount += value;
    }
}
