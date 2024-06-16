/*
 * Created 2019-1-23 17:36:00
 */
package cn.com.yting.kxy.web.legendarypet;

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
@Table(name = "legendary_pet_generation_record")
@Data
@WebMessageType
public class LegendaryPetGenerationRecord implements Serializable {

    @Id
    @Column(name = "definition_id")
    private long definitionId;
    @Column(name = "available_count", nullable = false)
    private int availableCount;
    @Column(name = "redeemed_count", nullable = false)
    private int redeemedCount;
    @Column(name = "serial_number", nullable = false)
    private int serialNumber;

    public void decreaseAvailableCount() {
        availableCount--;
    }

    public void increaseRedeemedCount() {
        redeemedCount++;
    }

    public void increaseSerialNumber() {
        serialNumber++;
    }
}
