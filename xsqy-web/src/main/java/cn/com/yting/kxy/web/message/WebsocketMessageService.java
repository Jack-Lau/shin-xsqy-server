/*
 * Created 2018-7-13 17:24:45
 */
package cn.com.yting.kxy.web.message;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.account.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author Azige
 */
@Service
public class WebsocketMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(WebsocketMessageService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 向一个用户发送消息
     *
     * @param accountId
     * @param destination
     * @param message
     */
    public void sendToUser(long accountId, String destination, Object message) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) {
            LOG.warn("尝试向不存在的用户发送消息，id={}", accountId);
        } else {
            messagingTemplate.convertAndSendToUser(account.getUsername(), "/queue" + destination, message);
        }
    }

    public void sendToAll(String destination, Object message) {
        messagingTemplate.convertAndSend("/topic" + destination, message);
    }
}
