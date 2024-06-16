/*
 * Created 2018-8-9 15:42:41
 */
package cn.com.yting.kxy.web.title;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "title", indexes = @Index(columnList = "account_id, definition_id"))
@Data
@WebMessageType
public class Title implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "account_id", nullable = false)
    private long accountId;
    @Column(name = "definition_id", nullable = false)
    private long definitionId;
    @Column(name = "number")
    private Integer number;
    @Column(name = "trade_lock_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tradeLockTime;

    public void verifyOwner(long accountId) {
        if (getAccountId() != accountId) {
            throw TitleException.notOwner();
        }
    }
}
