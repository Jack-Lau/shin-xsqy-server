/*
 * Created 2018-12-25 16:30:57
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
@Table(name = "consignment_equipment_info")
@Data
public class ConsignmentEquipmentInfo implements Serializable {

    @Id
    @OneToOne
    @JoinColumn(name = "consignment_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Consignment consignment;
    @Column(name = "part", nullable = false)
    private int part;
    @Column(name = "color", nullable = false)
    private int color;
    @Column(name = "patk", nullable = false)
    private int patk;
    @Column(name = "matk", nullable = false)
    private int matk;
    @Column(name = "fc", nullable = false)
    private long fc;
    @Column(name = "max_enhance_level", nullable = false)
    private int maxEnhanceLevel;
}
