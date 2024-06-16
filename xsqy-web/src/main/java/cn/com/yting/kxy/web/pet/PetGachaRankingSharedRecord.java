/*
 * Created 2018-10-12 16:56:02
 */
package cn.com.yting.kxy.web.pet;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.com.yting.kxy.web.repository.LongId;
import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "pet_gacha_ranking_shared_record")
@Data
@WebMessageType
public class PetGachaRankingSharedRecord implements Serializable, LongId {

    @Id
    private Long id;
    @Column(name = "remaining_yingting", nullable = false)
    private int remainingYingting;
    @Column(name = "next_yingting_number", nullable = false)
    private int nextYingtingNumber = 1;
}
