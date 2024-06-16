/*
 * Created 2018-6-27 15:09:06
 */
package cn.com.yting.kxy.web.currency;

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
@Data
@Table(name = "currency_change_log", indexes = @Index(columnList = "account_id, currency_id, event_time"))
public class CurrencyChangeLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "account_id", nullable = false)
    private long accountId;
    @Column(name = "currency_id", nullable = false)
    private long currencyId;
    @Column(name = "before_amount", nullable = false)
    private long beforeAmount;
    @Column(name = "after_amount", nullable = false)
    private long afterAmount;
    @Column(name = "event_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTime;
    @Column(name = "purpose")
    private Integer purpose;
}
