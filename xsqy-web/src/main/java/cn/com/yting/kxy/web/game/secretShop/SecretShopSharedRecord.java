/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.secretShop;

import cn.com.yting.kxy.web.repository.LongId;

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
@Table(name = "secret_shop_shared_record")
@Data
public class SecretShopSharedRecord implements Serializable, LongId {

    @Id
    private Long id;
    @Column(name = "kc_pack_remain_count")
    private int kcPackRemainCount;
    @Column(name = "kc_pack_price")
    private long kcPackPrice;

}
