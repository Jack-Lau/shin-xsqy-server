/*
 * Created 2018-8-10 11:06:54
 */
package cn.com.yting.kxy.web.player;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface PlayerRelationRepository extends JpaRepository<PlayerRelation, Long> {

    @Query("SELECT r FROM PlayerRelation r WHERE r.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PlayerRelation> findByIdForWrite(long accountId);

    default PlayerRelation findOrDummy(long accountId) {
        return findById(accountId).orElseGet(() -> {
            PlayerRelation playerRelation = new PlayerRelation();
            playerRelation.setAccountId(accountId);
            return playerRelation;
        });
    }

    default PlayerRelation findOrCreate(long accountId) {
        return findByIdForWrite(accountId).orElseGet(() -> {
            PlayerRelation playerRelation = new PlayerRelation();
            playerRelation.setAccountId(accountId);
            return saveAndFlush(playerRelation);
        });
    }
}
