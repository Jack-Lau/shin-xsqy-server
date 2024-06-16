/*
 * Created 2018-11-21 12:26:30
 */
package cn.com.yting.kxy.web.currency;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "currency_change_statistic")
@Data
@IdClass(CurrencyChangeStatistic.PK.class)
public class CurrencyChangeStatistic implements Serializable {

    @Id
    @Column(name = "statistic_date")
    @Temporal(TemporalType.DATE)
    private Date statisticDate;
    @Id
    @Column(name = "currency_id")
    private long currencyId;
    @Column(name = "total_gain", nullable = false)
    private long totalGain;
    @Column(name = "total_drain", nullable = false)
    private long totalDrain;
    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified = new Date();

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class PK implements Serializable {

        private Date statisticDate;
        private long currencyId;
    }
}
