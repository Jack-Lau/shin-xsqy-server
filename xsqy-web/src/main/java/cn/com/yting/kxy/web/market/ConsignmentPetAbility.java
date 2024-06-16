/*
 * Created 2018-12-26 17:56:00
 */
package cn.com.yting.kxy.web.market;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "consignment_pet_ability")
@Data
public class ConsignmentPetAbility implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "consignment_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Consignment consignment;
    @Id
    @Column(name = "ability_id")
    private long abilityId;
}
