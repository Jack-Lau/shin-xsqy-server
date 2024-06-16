/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.fishing;

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
 * @author Administrator
 */
@Entity
@Table(name = "fishing_once_record")
@Data
public class FishingOnceRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "fish_category_id")
    private long fishCategoryId;
    @Column(name = "gram")
    private long gram;
    @Column(name = "award_currency_id")
    private long awardCurrencyId;
    @Column(name = "award_currency_amount")
    private long awardCurrencyAmount;
    @Column(name = "finish")
    private boolean finish;
    @Column(name = "duration")
    private long duration;
    @Column(name = "finish_limit_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishLimitTime = new Date(0);

}
