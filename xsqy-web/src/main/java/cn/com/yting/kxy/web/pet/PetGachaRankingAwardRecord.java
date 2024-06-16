/*
 * Created 2018-10-12 16:33:13
 */
package cn.com.yting.kxy.web.pet;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "pet_gacha_ranking_award_record")
@Data
@WebMessageType
public class PetGachaRankingAwardRecord implements Serializable {

    @Id
    @Column(name = "ranking")
    private int ranking;
    @Column(name = "account_id", nullable = false)
    private long accountId;
    @Column(name = "final_point", nullable = false)
    private long finalPoint;
    @Column(name = "award", nullable = false)
    private String award;
    @Column(name = "delivered", nullable = false)
    private boolean delivered = false;
}
