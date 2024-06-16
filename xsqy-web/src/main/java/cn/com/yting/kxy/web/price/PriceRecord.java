/*
 * Created 2018-9-19 19:07:29
 */
package cn.com.yting.kxy.web.price;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "price_record")
@Data
public class PriceRecord implements Serializable {

    @Id
    private long id;
    @Column(name = "current_value")
    private long currentPrice;
    @Column(name = "used_count")
    private int usedCount;

    public void increaseUsedCount() {
        usedCount++;
    }

    public void increaseUsedCount(int value) {
        usedCount += value;
    }
}
