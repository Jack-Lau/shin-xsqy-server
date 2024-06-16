/*
 * Created 2018-6-26 17:25:19
 */
package cn.com.yting.kxy.web.currency;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

import cn.com.yting.kxy.web.currency.CurrencyRecord.CurrencyRecordPK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import cn.com.yting.kxy.web.message.WebMessageType;

/**
 *
 * @author Azige
 */
@Entity
@Data
@Table(name = "currency_record", indexes = @Index(columnList = "currency_id"))
@IdClass(CurrencyRecordPK.class)
@WebMessageType
public class CurrencyRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Id
    @Column(name = "currency_id")
    private long currencyId;
    @Column(name = "amount", nullable = false)
    private long amount = 0;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrencyRecordPK implements Serializable {

        private long accountId;
        private long currencyId;
    }
}
