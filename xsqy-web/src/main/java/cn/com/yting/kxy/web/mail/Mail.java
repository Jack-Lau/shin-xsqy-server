/*
 * Created 2018-7-20 15:53:24
 */
package cn.com.yting.kxy.web.mail;

import cn.com.yting.kxy.web.currency.CurrencyConstants;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.message.WebMessageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "mail", indexes = {
    @Index(columnList = "account_id")
    ,
    @Index(columnList = "create_time")
})
@Data
@WebMessageType
public class Mail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "account_id", nullable = false)
    private long accountId;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Column(name = "already_read", nullable = false)
    private boolean alreadyRead = false;
    /**
     * 附件只能包含货币
     */
    @JsonIgnore
    @Column(name = "attachment", nullable = false)
    private String attachment;
    @Column(name = "attachment_delivered", nullable = false)
    private boolean attachmentDelivered = false;
    @Column(name = "attachment_source", nullable = false)
    private int attachmentSource = CurrencyConstants.PURPOSE_INCREMENT_邮件_未指定块币附件来源;

    public List<CurrencyStack> getAttachmentAsCurrencyStacks() {
        return CurrencyStack.listFromText(attachment);
    }

}
