/*
 * Created 2018-7-9 16:14:55
 */
package cn.com.yting.kxy.web.invitation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Azige
 */
public interface InvitationRepository extends JpaRepository<InvitationRecord, InvitationRecord.PK> {

    boolean existsByAccountId(long accountId);

    List<InvitationRecord> findByAccountId(long accountId);

    List<InvitationRecord> findByInviterId(long inviterId);

    List<InvitationRecord> findByInviterIdAndInviterDepth(long inviterId, int inviterDepth);

    default List<InvitationRecord> findDirectInvitationByInviterId(long inviterId) {
        return findByInviterIdAndInviterDepth(inviterId, InvitationConstants.DIRECT_INVITATION_DEPTH);
    }

    int countByInviterIdAndInviterDepth(long inviterId, int inviterDepth);
}
