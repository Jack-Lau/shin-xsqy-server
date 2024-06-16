/*
 * Created 2018-12-25 16:32:54
 */
package cn.com.yting.kxy.web.market;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "consignment_pet_info")
@Data
public class ConsignmentPetInfo implements Serializable {

    @Id
    @OneToOne
    @JoinColumn(name = "consignment_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Consignment consignment;
    @Column(name = "aptitude_hp", nullable = false)
    private int aptitudeHp;
    @Column(name = "aptitude_atk", nullable = false)
    private int aptitudeAtk;
    @Column(name = "aptitude_pdef", nullable = false)
    private int aptitudePdef;
    @Column(name = "aptitude_mdef", nullable = false)
    private int aptitudeMdef;
    @Column(name = "aptitude_spd", nullable = false)
    private int aptitudeSpd;
    @Column(name = "pet_rank", nullable = false)
    private int petRank;
    @Column(name = "max_pet_rank", nullable = false)
    private int maxPetRank;
}
