/*
 * Created 2018-8-1 18:55:10
 */
package cn.com.yting.kxy.web.quest;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import cn.com.yting.kxy.web.quest.QuestRecord.QuestRecordPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface QuestRepository extends JpaRepository<QuestRecord, QuestRecordPK> {

    default Optional<QuestRecord> findById(long accountId, long questId) {
        return findById(new QuestRecordPK(accountId, questId));
    }

    @Query("SELECT q FROM QuestRecord q WHERE q.accountId = ?1 AND q.questId = ?2")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<QuestRecord> findByIdForWrite(long accountId, long questId);

    List<QuestRecord> findByAccountId(long accountId);

    @Modifying
    @Query("UPDATE QuestRecord q SET q.startedCount = 0 WHERE q.questId = ?1")
    int resetStartedCountByQuestId(long questId);

    @Query("SELECT p.accountId"
        + " FROM Player p"
        + " LEFT JOIN QuestRecord q ON p.accountId = q.accountId AND q.questId = ?1"
        + " WHERE q.questStatus IS NULL OR q.questStatus = 'NOT_STARTED_YET'")
    List<Long> findAccountIdsByNotStartedQuest(long questId);

    @Modifying
    @Query("DELETE FROM QuestRecord q WHERE q.questId >= 730111 AND q.questId <= 730150")
    void deleteFxjlQuests();
}
