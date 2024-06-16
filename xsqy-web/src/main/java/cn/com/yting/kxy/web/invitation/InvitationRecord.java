/*
 * Created 2018-7-9 15:22:54
 */
package cn.com.yting.kxy.web.invitation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "invitation_record", indexes = @Index(columnList = "inviter_id, inviter_depth"))
@IdClass(InvitationRecord.PK.class)
@Data
public class InvitationRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Id
    @Column(name = "inviter_id")
    private long inviterId;
    /**
     * 邀请者的传递深度，直接邀请者为 1
     */
    @Column(name = "inviter_depth", nullable = false)
    private int inviterDepth;

    public boolean isDirectInvitation() {
        return inviterDepth == InvitationConstants.DIRECT_INVITATION_DEPTH;
    }

    @Data
    public static class PK implements Serializable {

        private long accountId;
        private long inviterId;
    }
}
