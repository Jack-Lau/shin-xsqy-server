/*
 * Created 2018-9-26 16:27:54
 */
package cn.com.yting.kxy.web.party;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "support_log", indexes = @Index(columnList = "supporter_account_id,event_time"))
@Data
@WebMessageType
public class SupportLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "inviter_account_id", nullable = false)
    private long inviterAccountId;
    @Column(name = "supporter_account_id", nullable = false)
    private long supporterAccountId;
    @Column(name = "event_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventTime;
    @Column(name = "fee", nullable = false)
    private long fee;
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SupportLogType type;
}
