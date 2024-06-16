/*
 * Created 2018-11-20 12:45:01
 */
package cn.com.yting.kxy.web.impartation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "impartation_record")
@Data
@WebMessageType
public class ImpartationRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "impartation_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private ImpartationRole role;
}
