/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.shop;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Entity
@Table(name = "shop_commodity")
@Data
public class ShopCommodityRecord implements Serializable {

    @Id
    @Column(name = "commodity_id")
    private long commodityId;
    @Column(name = "current_price")
    private long currentPrice;
    @Column(name = "remain_count")
    private long remainCount;
    @Column(name = "total_buy")
    private long totalBuy;
    @Column(name = "total_buy_in_period")
    private long totalBuyInPeriod;

}
