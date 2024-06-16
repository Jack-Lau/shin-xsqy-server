/*
 * Created 2018-7-19 16:15:59
 */
package cn.com.yting.kxy.web.chat;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.wordfilter.ForbiddenWordsChecker;
import cn.com.yting.kxy.core.wordfilter.SearchResult;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.account.AccountRepository;
import cn.com.yting.kxy.web.chat.model.BroadcastInfo;
import cn.com.yting.kxy.web.chat.model.BroadcastRecord;
import cn.com.yting.kxy.web.chat.model.BroadcastRepository;
import cn.com.yting.kxy.web.chat.model.ChatElement;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.chat.model.TemplateElement;
import cn.com.yting.kxy.web.chat.model.TextElement;
import cn.com.yting.kxy.web.friend.FriendService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class ChatService implements ResetTask {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PrivateMessageRepository privateMessageRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private BroadcastRepository broadcastRepository;

    @Autowired
    @Lazy
    private ChatService self;
    @Autowired
    private FriendService friendService;

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ForbiddenWordsChecker forbiddenWordsChecker;
    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private ResourceContext resourceContext;

    private final Queue<ChatMessage> latestMessages = EvictingQueue.create(30);
    /**
     * {@code 账号id -> 最后发送消息时间} 的映射，用于限制玩家发送消息的时间间隔
     */
    private final Cache<Long, AtomicLong> lastSendingTimeCache = CacheBuilder.newBuilder()
            .expireAfterAccess(ChatConstants.INTERVAL_LIMIT_PUBLIC_MESSAGE * 2, TimeUnit.MILLISECONDS)
            .build();

    private final Object sendingBroadcastConfigLock = new Object();
    private SendingBroadcastSetting sendingBroadcastSetting;
    private ScheduledFuture<?> sendingBroadcastTask;
    private final Map<Long, Queue<ChatMessage>> latestInterestingMessageMap = new HashMap<>();

    public SendingBroadcastSetting getSendingBroadcastState() {
        return sendingBroadcastSetting;
    }

    public SendingBroadcastSetting createSendingBroadcastTask(String message, long interval) {
        synchronized (sendingBroadcastConfigLock) {
            if (sendingBroadcastTask != null) {
                sendingBroadcastTask.cancel(false);
            }

            sendingBroadcastSetting = new SendingBroadcastSetting(message, interval);
            sendingBroadcastTask = taskScheduler.scheduleAtFixedRate(() -> {
                ChatMessage m = new ChatMessage();
                m.setElements(Arrays.asList(new TextElement(message)));
                self.sendSystemMessage(ChatConstants.SERVICE_ID_GAME_MASTER, m);
            }, Duration.ofSeconds(interval));
            return sendingBroadcastSetting;
        }
    }

    public void cancelSendingBroadcastTask() {
        synchronized (sendingBroadcastConfigLock) {
            if (sendingBroadcastTask != null) {
                sendingBroadcastTask.cancel(false);

                sendingBroadcastSetting = null;
                sendingBroadcastTask = null;
            }
        }
    }

    public Collection<ChatMessage> getLatestMessages() {
        return latestMessages;
    }

    public ChatMessage sendPlayerMessage(long accountId, ChatMessage message) {
        return sendPlayerMessage(accountId, message, true);
    }

    public ChatMessage sendPlayerMessage(long accountId, ChatMessage message, boolean checkSendInterval) {
        Player player = playerRepository.findById(accountId).get();
        if (message.isBroadcast() && player.getPlayerLevel() < 30) {
            throw KxyWebException.unknown("角色等级不足");
        }
        if (!message.isBroadcast() && !friendService.friend(accountId, message.getReceiverId())) {
            throw KxyWebException.unknown("不是对方的好友");
        }
        try {
            long currentTime = timeProvider.currentTime();
            AtomicLong lastSendingTime = lastSendingTimeCache.get(accountId, () -> new AtomicLong(0));
            synchronized (lastSendingTime) {
                if (checkSendInterval) {
                    if (message.isBroadcast()) {
                        if (currentTime - lastSendingTime.get() < ChatConstants.INTERVAL_LIMIT_PUBLIC_MESSAGE) {
                            throw ChatException.overFrequencyLimit();
                        }
                    } else {
                        if (currentTime - lastSendingTime.get() < ChatConstants.INTERVAL_LIMIT_PRIVATE_MESSAGE) {
                            throw ChatException.overFrequencyLimit();
                        }
                    }
                }
                lastSendingTime.getAndSet(currentTime);

                message.setEventTime(new Date(currentTime));
                message.setSystemMessage(false);
                message.setSenderId(accountId);
                message.setElements(verifyAndRewriteChatElements(accountId, message.getElements()));

                sendMessage(message);

                return message;
            }
        } catch (ExecutionException ex) {
            throw KxyWebException.unknown(ex.getMessage(), ex);
        }
    }

    public ChatMessage sendSystemMessage(long serviceId, ChatMessage message) {
        message.setEventTime(new Date(timeProvider.currentTime()));
        message.setSystemMessage(true);
        // 系统消息只能是广播消息
        message.setBroadcast(true);
        message.setSenderId(serviceId);
        // 判断是否达到当日发送上限
        if (resourceContext.getLoader(BroadcastInfo.class).exists(message.getBroadcastId())) {
            BroadcastRecord br = findOrCreateBroadcastRecord(message.getBroadcastId());
            BroadcastInfo bi = resourceContext.getLoader(BroadcastInfo.class).get(message.getBroadcastId());
            if (bi.getUpperLimit() > 0) {
                if (br.getTodaySendCount() < bi.getUpperLimit()) {
                    br.setTodaySendCount(br.getTodaySendCount() + 1);
                    broadcastRepository.save(br);
                    sendMessage(message);
                }
            } else {
                sendMessage(message);
            }
        } else {
            sendMessage(message);
        }
        //
        return message;
    }

    private BroadcastRecord findOrCreateBroadcastRecord(long broadcastId) {
        BroadcastRecord br = broadcastRepository.findByBroadcastId(broadcastId);
        if (br == null) {
            br = new BroadcastRecord();
            br.setBroadcastId(broadcastId);
            br.setTodaySendCount(0);
        }
        return br;
    }

    private void sendMessage(ChatMessage message) {
        if (message.isBroadcast()) {
            message.setId(UUID.randomUUID().toString());
            latestMessages.offer(message);
        } else {
            verifyReceiverAccountId(message.getSenderId(), message.getReceiverId());
            PrivateMessage privateMessage = PrivateMessage.createFromChatMessage(message);
            privateMessage = privateMessageRepository.saveAndFlush(privateMessage);
            message.setId(String.valueOf(privateMessage.getId()));
            privateMessageRepository.deleteAll(privateMessageRepository.findBySenderAccountIdAndReceiverAccountId(
                    privateMessage.getSenderAccountId(),
                    privateMessage.getReceiverAccountId(),
                    PageRequest.of(1, ChatConstants.PRIVATE_MESSAGE_MAX_COUNT, Direction.DESC, "id")
            ));
        }
        eventPublisher.publishEvent(new ChatMessageSentEvent(this, message));
    }

    public List<ChatMessage> getConversation(long accountId, long anotherAccountId) {
        return privateMessageRepository.findConversation(accountId, anotherAccountId)
                .map(PrivateMessage::toChatMessage)
                .collect(Collectors.toList());
    }

    public void markAlreadyRead(long senderAccountId, long receiverAccountId) {
        privateMessageRepository.markAsAlreadyReadBySenderAccountIdAndReceiverAccountId(senderAccountId, receiverAccountId);
    }

    private Queue<ChatMessage> getOrCreateInterestingMessageQueue(long broadCastId) {
        synchronized (latestInterestingMessageMap) {
            Queue<ChatMessage> queue = latestInterestingMessageMap.get(broadCastId);
            if (queue == null) {
                queue = EvictingQueue.create(30);
                latestInterestingMessageMap.put(broadCastId, queue);
            }
            return queue;
        }
    }

    public void offerInterestingMessage(long broadcastId, ChatMessage message) {
        Queue<ChatMessage> queue = getOrCreateInterestingMessageQueue(broadcastId);
        synchronized (queue) {
            queue.offer(message);
        }
    }

    public List<ChatMessage> getInterestingMessages(long broadcastId) {
        Queue<ChatMessage> queue = latestInterestingMessageMap.get(broadcastId);
        if (queue == null) {
            return Collections.emptyList();
        }
        synchronized (queue) {
            return ImmutableList.copyOf(queue);
        }
    }

    private List<ChatElement<?>> verifyAndRewriteChatElements(long accountId, List<ChatElement<?>> elements) {
        return elements.stream()
                .map(element -> {
                    if (element instanceof TextElement) {
                        TextElement textElement = (TextElement) element;
                        String content = textElement.getContent();
                        List<SearchResult> forbbidenWordsResults = forbiddenWordsChecker.searchWords(content);
                        if (!forbbidenWordsResults.isEmpty()) {
                            StringBuilder sb = new StringBuilder(content);
                            for (SearchResult result : forbbidenWordsResults) {
                                for (int i = 0; i < result.getWord().length(); i++) {
                                    sb.setCharAt(result.getIndex() + i, '*');
                                }
                            }
                            element = new TextElement(sb.toString());
                        }
                    } // TODO
                    //                else if (element instanceof EmoticonElement) {
                    //                }
                    else if (element instanceof TemplateElement) {
                        throw ChatException.illegalElementType(TemplateElement.class.getSimpleName());
                    }
                    return element;
                })
                .collect(Collectors.toList());
    }

    private void verifyReceiverAccountId(long senderId, long receiverId) {
        if (senderId == receiverId) {
            throw ChatException.illegalReceiver("不能发消息给自己");
        }
        if (!accountRepository.existsById(receiverId)) {
            throw ChatException.illegalReceiver("账号不存在：" + receiverId);
        }
    }

    @Override
    public void dailyReset() {
        List<BroadcastRecord> brList = broadcastRepository.findAll();
        brList.forEach((br) -> {
            br.setTodaySendCount(0);
        });
        broadcastRepository.saveAll(brList);
    }

}
