/*
 * Created 2018-7-7 11:25:54
 */
package cn.com.yting.kxy.web.game.kuaibidazhuanpan;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.com.yting.kxy.web.repository.LongId;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "kbdzp_shared_record")
@Data
public class KbdzpSharedRecord implements Serializable, LongId {

    @Id
    private Long id;
    @Column(name = "booster1_activation_code")
    private String booster1ActivationCode;
    @Column(name = "booster2_activation_code")
    private String booster2ActivationCode;
    @Column(name = "public_pool", nullable = false)
    private long publicPool = KbdzpConstants.PUBLIC_POOL_RECOVER_PER_HOUR;
}
