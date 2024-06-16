/*
 * Created 2018-9-26 15:46:02
 */
package cn.com.yting.kxy.web.party;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.LockModeType;

import cn.com.yting.kxy.web.repository.MapRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Azige
 */
@Repository
public class SupportRelationRepository extends MapRepository<SupportRelation> {

    @Query("SELECT r FROM SupportRelation r WHERE r.inviterAccountId = ?1 AND r.supporterAccountId = ?2")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public SupportRelation findByIdForWrite(long inviterAccountId, long supporterAccountId) {
        return getContainerMap().values().stream()
            .filter(it -> it.getInviterAccountId() == inviterAccountId && it.getSupporterAccountId() == supporterAccountId)
            .findAny().orElse(null);
    }

    public List<SupportRelation> findByInviterAccountId(long inviterAccountId) {
        return getContainerMap().values().stream()
            .filter(it -> it.getInviterAccountId() == inviterAccountId)
            .collect(Collectors.toList());
    }

    @Query("SELECT r FROM SupportRelation r WHERE r.inviterAccountId = ?1 AND r.released IS FALSE")
    public List<SupportRelation> findPartyMembers(long inviterAccountId) {
        return getContainerMap().values().stream()
            .filter(it -> it.getInviterAccountId() == inviterAccountId && !it.isReleased())
            .collect(Collectors.toList());
    }

    @Query("SELECT COUNT(*) FROM SupportRelation r WHERE r.supporterAccountId = ?1 AND r.released IS FALSE")
    public int countBySupporter(long supporterAccountId) {
        return (int) getContainerMap().values().stream()
            .filter(it -> it.getSupporterAccountId() == supporterAccountId && !it.isReleased())
            .count();
    }

    @Query("SELECT r FROM SupportRelation r WHERE r.releaseCooldown IS NULL AND r.deadline < ?1 OR r.releaseCooldown < ?1")
    List<SupportRelation> findExpired(Date currentTime) {
        return getContainerMap().values().stream()
            .filter(it -> it.getReleaseCooldown() == null && it.getDeadline() != null && it.getDeadline().before(currentTime) || it.getReleaseCooldown() != null && it.getReleaseCooldown().before(currentTime))
            .collect(Collectors.toList());
    }
}
