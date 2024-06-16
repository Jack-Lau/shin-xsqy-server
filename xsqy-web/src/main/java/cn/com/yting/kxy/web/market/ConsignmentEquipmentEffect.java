/*
 * Created 2018-12-25 17:45:06
 */
package cn.com.yting.kxy.web.market;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
@Table(name = "consignment_equipment_effect")
@Data
@IdClass(ConsignmentEquipmentEffect.PK.class)
public class ConsignmentEquipmentEffect implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "consignment_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Consignment consignment;
    @Id
    @Column(name = "effect_id")
    private long effectId;

    @Data
    public static class PK implements Serializable {

        private long consignment;
        private long effectId;
    }
}
