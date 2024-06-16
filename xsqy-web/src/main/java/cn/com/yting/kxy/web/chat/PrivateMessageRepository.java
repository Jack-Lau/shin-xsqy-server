/*
 * Created 2018-8-6 16:45:54
 */
package cn.com.yting.kxy.web.chat;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Long> {

    List<PrivateMessage> findBySenderAccountIdAndReceiverAccountId(long senderAccountId, long receiverAccountId, Pageable pageable);

    default Stream<PrivateMessage> findConversation(long accountId, long anotherAccountId) {
        return findConversation(PrivateMessage.createConversationString(accountId, anotherAccountId));
    }

    @Query(value = "SELECT * FROM private_message pm WHERE pm.conversation = ?1 ORDER BY id DESC",
            nativeQuery = true)
    Stream<PrivateMessage> findConversation(String conversation);

    @Modifying
    @Query("UPDATE PrivateMessage pm SET pm.alreadyRead = TRUE WHERE pm.senderAccountId = ?1 AND pm.receiverAccountId = ?2")
    void markAsAlreadyReadBySenderAccountIdAndReceiverAccountId(long senderAccountId, long receiverAccountId);

    @Query("SELECT NEW cn.com.yting.kxy.web.chat.PrivateMessageIncomingInfo(pm.senderAccountId, COUNT(pm))"
            + " FROM PrivateMessage pm"
            + " WHERE pm.receiverAccountId = ?1 AND pm.alreadyRead IS FALSE"
            + " GROUP BY pm.senderAccountId")
    List<PrivateMessageIncomingInfo> findPrivateMessageIncomings(long accountId);

    default PrivateMessage findLastPrivateMessage(long accountId, long anotherAccountId) {
        return findLastPrivateMessage(PrivateMessage.createConversationString(accountId, anotherAccountId));
    }

    @Query(value = "SELECT * FROM private_message pm WHERE pm.conversation = ?1 ORDER BY id DESC LIMIT 0,1",
            nativeQuery = true)
    PrivateMessage findLastPrivateMessage(String conversation);

}
