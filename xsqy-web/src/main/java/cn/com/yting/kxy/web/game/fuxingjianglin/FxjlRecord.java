/*
 * Created 2019-1-21 17:56:15
 */
package cn.com.yting.kxy.web.game.fuxingjianglin;

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
@Table(name = "fxjl_record")
@Data
@WebMessageType
public class FxjlRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "award_delivered", nullable = false)
    private boolean awardDelivered;
}
