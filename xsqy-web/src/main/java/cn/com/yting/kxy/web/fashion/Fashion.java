/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.fashion;

import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.message.WebMessageType;
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
 * @author Darkholme
 */
@Entity
@Table(name = "fashion", indexes = {
    @Index(columnList = "account_id")
    ,
    @Index(columnList = "nft_id")
})
@Data
@WebMessageType
public class Fashion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "definition_id", nullable = false)
    private long definitionId;
    @Column(name = "dye_id")
    private long dyeId;
    @Column(name = "number")
    private Integer number;
    @Column(name = "nft_id", unique = true)
    private Long nftId;
    @Column(name = "next_withdraw_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextWithdrawTime;

    public void verifyOwner(long accountId) throws KxyWebException {
        if (this.accountId != accountId) {
            throw KxyWebException.unknown("不是时装的所有者");
        }
    }

}
