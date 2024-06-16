/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.secretShop;

import cn.com.yting.kxy.web.message.WebMessageType;
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
@Table(name = "secret_shop_prize_granting_stats")
@Data
@WebMessageType
public class SecretShopPrizeGrantingStats implements Serializable {

    @Id
    @Column(name = "id")
    private long id;
    @Column(name = "granted_count")
    private int grantedCount;

    public void increaseGrantedCount() {
        grantedCount++;
    }

}
