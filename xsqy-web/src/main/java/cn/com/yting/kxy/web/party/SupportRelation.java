/*
 * Created 2018-9-26 15:37:39
 */
package cn.com.yting.kxy.web.party;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.repository.LongId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 助战关系记录。
 * 如果助战关系记录存在且 released 不为 true 则表示助战者在邀请者的队伍中；
 * 如果 released 为 true 则表示助战者已被邀请者手动解除助战关系。
 * 存在助战关系时不能再邀请此助战者。
 * 应当定期清理已超过期限的助战关系。
 *
 * @author Azige
 */
@Entity
@Table(name = "support_relation", indexes = @Index(columnList = "supporter_account_id"))
@IdClass(SupportRelation.PK.class)
@Data
@WebMessageType
public class SupportRelation implements Serializable, LongId {

    private Long id;

    @Id
    @Column(name = "inviter_account_id")
    private long inviterAccountId;
    @Id
    @Column(name = "supporter_account_id")
    private long supporterAccountId;
    @Column(name = "deadline", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;
    @Column(name = "released", nullable = false)
    private boolean released;
    @Column(name = "release_cooldown")
    @Temporal(TemporalType.TIMESTAMP)
    private Date releaseCooldown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {

        private long inviterAccountId;
        private long supporterAccountId;
    }
}
