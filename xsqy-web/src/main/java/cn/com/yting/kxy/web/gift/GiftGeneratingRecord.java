/*
 * Created 2017-6-27 11:11:18
 */
package cn.com.yting.kxy.web.gift;

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
@Table(name = "gift_generating_record")
@Data
public class GiftGeneratingRecord implements Serializable {

    @Id
    private long id;
    @Column(name = "prototype_code", nullable = false)
    private String prototypeCode;
    @Column(name = "serial_code_begin", nullable = false)
    private int serialCodeBegin;
    @Column(name = "generated_count", nullable = false)
    private int generatedCount;

    public GiftCodeGenerator createGenerator() {
        return new GiftCodeGenerator(prototypeCode, serialCodeBegin, generatedCount);
    }
}
