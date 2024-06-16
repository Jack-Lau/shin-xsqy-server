/*
 * Created 2019-1-23 15:08:38
 */
package cn.com.yting.kxy.web.game.zaixianjiangli;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.com.yting.kxy.web.message.WebMessageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "zxjl_record")
@Data
@WebMessageType
public class ZxjlRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "award_1_delivered", nullable = false)
    @JsonIgnore
    private boolean award_1_delivered;
    @Column(name = "award_2_delivered", nullable = false)
    @JsonIgnore
    private boolean award_2_delivered;
    @Column(name = "award_3_delivered", nullable = false)
    @JsonIgnore
    private boolean award_3_delivered;
    @Column(name = "award_4_delivered", nullable = false)
    @JsonIgnore
    private boolean award_4_delivered;
    @Column(name = "award_5_delivered", nullable = false)
    @JsonIgnore
    private boolean award_5_delivered;

    public List<Boolean> getAwardsDelivered() {
        return Arrays.asList(award_1_delivered, award_2_delivered, award_3_delivered, award_4_delivered, award_5_delivered);
    }
}
