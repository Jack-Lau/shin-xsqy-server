/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.changlefang;

import cn.com.yting.kxy.web.message.WebMessageType;
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
@Table(name = "changlefang_shared_record")
@Data
@WebMessageType
public class ChanglefangSharedRecord implements Serializable, LongId {

    @Id
    private Long id;
    @Column(name = "total_share")
    private int totalShare;
    @Column(name = "day_share")
    private int dayShare;
    @Column(name = "day_energy")
    private long dayEnergy;
    @Column(name = "last_day_award_energy")
    private Long lastDayAwardEnergy;

}
