/*
 * Created 2016-2-26 18:41:31
 */
package cn.com.yting.kxy.web.party;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.util.CommaSeparatedLists;
import cn.com.yting.kxy.core.util.TimeUtils;
import cn.com.yting.kxy.web.battle.BattleUnitExporter;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.friend.Friend;
import cn.com.yting.kxy.web.friend.FriendService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class PartyService implements ResetTask {

    private static final Logger LOG = LoggerFactory.getLogger(PartyService.class);

    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private SupportRelationRepository supportRelationRepository;
    @Autowired
    private SupportLogRepository invitationLogRepository;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private FriendService friendService;

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private BattleUnitExporter battleUnitExporter;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public PartyRecord requestCandidates(long accountId, boolean highLevel) {
        Player player = playerRepository.findById(accountId).get();
        long baseFc = player.getFc();
        List<Long> candidateIds;
        if (highLevel) {
            candidateIds = playerRepository.findAccountIdByCandidateSupporterCondition(
                    accountId,
                    (long) (baseFc * 1.5 + 1000),
                    (long) (baseFc * 2 + 2000),
                    PartyConstants.MAX_SUPPORT_COUNT
            );
            if (candidateIds.size() < PartyConstants.CANDIDATE_LIMIT) {
                candidateIds = playerRepository.findAccountIdByCandidateSupporterCondition(
                        accountId,
                        (long) (baseFc * 1.4 + 1000),
                        (long) (baseFc * 2 + 5000),
                        PartyConstants.MAX_SUPPORT_COUNT
                );
            }
        } else {
            candidateIds = playerRepository.findAccountIdByCandidateSupporterCondition(
                    accountId,
                    (long) (baseFc * 0.8),
                    (long) (baseFc * 1.2 + 1000),
                    PartyConstants.MAX_SUPPORT_COUNT
            );
            if (candidateIds.size() < PartyConstants.CANDIDATE_LIMIT) {
                candidateIds = playerRepository.findAccountIdByCandidateSupporterCondition(
                        accountId,
                        (long) (baseFc * 0.65),
                        (long) (baseFc * 1.35 + 1000),
                        PartyConstants.MAX_SUPPORT_COUNT
                );
            }
        }

        if (candidateIds.size() > PartyConstants.CANDIDATE_LIMIT) {
            Collections.shuffle(candidateIds);
            candidateIds = candidateIds.subList(0, PartyConstants.CANDIDATE_LIMIT);
        }
        PartyRecord record = partyRepository.findOrCreateById(accountId);
        record.setCandidateSupporters(CommaSeparatedLists.toText(candidateIds));
        record.setHighLevelCandidate(highLevel);

        return record;
    }

    public PartyRecord requestCandidatesInFriends(long accountId, boolean highLevel) {
        Player player = playerRepository.findById(accountId).get();
        List<Friend> friends = friendService.get(accountId);
        long baseFc = player.getFc();
        long fcUpperLimit = 0, fcLowerLimit = 0;
        if (highLevel) {
            fcUpperLimit = (long) (baseFc * 2 + 5000);
            fcLowerLimit = (long) (baseFc * 1.4 + 1000);
        } else {
            fcUpperLimit = (long) (baseFc * 1.35 + 1000);
            fcLowerLimit = 1;
        }
        List<Long> candidateIds = new ArrayList<>();
        for (Friend friend : friends) {
            Player p = friend.getPlayerBaseInfo().getPlayer();
            if (p.getFc() >= fcLowerLimit && p.getFc() <= fcUpperLimit) {
                candidateIds.add(p.getAccountId());
            }
        }

        if (candidateIds.size() > PartyConstants.CANDIDATE_LIMIT) {
            Collections.shuffle(candidateIds);
            candidateIds = candidateIds.subList(0, PartyConstants.CANDIDATE_LIMIT);
        }
        PartyRecord record = partyRepository.findOrCreateById(accountId);
        record.setCandidateSupporters(CommaSeparatedLists.toText(candidateIds));
        record.setHighLevelCandidate(highLevel);

        return record;
    }

    public SupportRelation invite(long accountId, long targetAccountId) {
        PartyRecord record = partyRepository.findOrCreateById(accountId);
        List<Long> candidateIds = CommaSeparatedLists.fromText(record.getCandidateSupporters(), Long::valueOf);
        if (!candidateIds.contains(targetAccountId)) {
            throw PartyException.invalidCandidate();
        }
        Player player = playerRepository.findById(accountId).get();
        if (player.getPlayerLevel() < PartyConstants.REQUIREMENT_PLAYER_LEVEL) {
            throw PartyException.levelRequirementNotMeet();
        }
        if (supportRelationRepository.countBySupporter(targetAccountId) >= PartyConstants.MAX_SUPPORT_COUNT) {
            throw PartyException.maxSupportCountReached();
        }
        List<SupportRelation> supportRelations = supportRelationRepository.findByInviterAccountId(accountId);
        if (supportRelations.stream().filter(it -> !it.isReleased()).count() >= PartyConstants.MAX_PARTY_MEMBER) {
            throw PartyException.maxPartyMemberReached();
        }
        supportRelations.forEach(it -> {
            if (it.getSupporterAccountId() == targetAccountId) {
                if (it.isReleased()) {
                    throw PartyException.targetInCooldown();
                } else {
                    throw PartyException.targetAlreadyInParty();
                }
            }
        });
        Player targetPlayer = playerRepository.findById(targetAccountId).get();
        long fc = targetPlayer.getFc();
        long fee = Math.max((long) (PartyConstants.INVITATION_FEE_BASE + Math.pow((double) fc / 100.0, 2.0) * PartyConstants.INVITATION_FEE_FC_RATE), 1);
        if (record.isHighLevelCandidate()) {
            fee *= PartyConstants.INVITATION_FEE_HIGH_LEVEL_RATE;
        }
        if (currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_元宝).getAmount() < fee) {
            throw PartyException.insufficientCurrency();
        }

        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, fee, true);
        SupportRelation supportRelation = supportRelationRepository.findByIdForWrite(accountId, targetAccountId);
        if (supportRelation == null) {
            supportRelation = new SupportRelation();
            supportRelation.setInviterAccountId(accountId);
            supportRelation.setSupporterAccountId(targetAccountId);
        }
        supportRelation.setDeadline(Date.from(timeProvider.currentInstant().plus(PartyConstants.DURATION_SUPPORT)));
        supportRelation.setReleased(false);
        supportRelation.setReleaseCooldown(null);
        supportRelation = supportRelationRepository.save(supportRelation);

        battleUnitExporter.createSupportUnit(accountId, targetAccountId);

        SupportLog log = new SupportLog();
        log.setInviterAccountId(accountId);
        log.setSupporterAccountId(targetAccountId);
        log.setEventTime(new Date(timeProvider.currentTime()));
        log.setFee(fee);
        log.setType(SupportLogType.START);
        invitationLogRepository.saveAndFlush(log);

        eventPublisher.publishEvent(new PartyMemberInvitedEvent(this, accountId));

        return supportRelation;
    }

    public void release(long accountId, long supporterAccountId) {
        SupportRelation supportRelation = supportRelationRepository.findByIdForWrite(accountId, supporterAccountId);
        if (supportRelation == null || supportRelation.isReleased()) {
            throw PartyException.notInParty();
        }
        supportRelation.setReleased(true);
        supportRelation.setReleaseCooldown(Date.from(timeProvider.currentInstant().plus(PartyConstants.DURATION_RELEASED_COOLDOWN)));

        SupportLog log = new SupportLog();
        log.setInviterAccountId(supportRelation.getInviterAccountId());
        log.setSupporterAccountId(supportRelation.getSupporterAccountId());
        log.setEventTime(new Date(timeProvider.currentTime()));
        log.setFee(0);
        log.setType(SupportLogType.END);
        invitationLogRepository.saveAndFlush(log);
    }

    public PartyRecord resolveSupportReward(long accountId) {
        PartyRecord record = partyRepository.findOrCreateById(accountId);
        OffsetDateTime lastResolveTime = TimeUtils.toOffsetTime(record.getLastRewardResolveTime());
        OffsetDateTime currentTime = timeProvider.currentOffsetDateTime();
        if (!lastResolveTime.toLocalDate().isBefore(currentTime.toLocalDate())) {
            return record;
        }

        LocalDate yesterday = timeProvider.yesterday();
        long sum = invitationLogRepository.getFeeSumOfDate(accountId, yesterday);
        long reward = (long) (sum * PartyConstants.REWARD_RATE);
        reward = Math.min(reward, PartyConstants.DAILY_REWARD_GOLD_MAX);
        record.setSupportReward(reward);
        record.setLastRewardResolveTime(new Date(timeProvider.currentTime()));
        record.setTodayRewardDelivered(false);

        return record;
    }

    public PartyRecord obtainSupportReward(long accountId) {
        PartyRecord record = partyRepository.findOrCreateById(accountId);
        if (record.isTodayRewardDelivered()) {
            throw PartyException.supportRewardAlreadyDelivered();
        }

        currencyService.increaseCurrency(accountId, CurrencyConstants.ID_元宝, record.getSupportReward());
        record.setTodayRewardDelivered(true);

        return record;
    }

    @Scheduled(cron = "20 * * * * *")
    public void deleteExpireSupportRelations() {
        List<SupportRelation> expiredRelations = supportRelationRepository.findExpired(new Date(timeProvider.currentTime()));

        Date currentTime = new Date(timeProvider.currentTime());
        for (SupportRelation expiredRelation : expiredRelations) {
            if (!expiredRelation.isReleased()) {
                SupportLog log = new SupportLog();
                log.setInviterAccountId(expiredRelation.getInviterAccountId());
                log.setSupporterAccountId(expiredRelation.getSupporterAccountId());
                log.setEventTime(currentTime);
                log.setFee(0);
                log.setType(SupportLogType.END);
                invitationLogRepository.save(log);
            }
        }
        invitationLogRepository.flush();

        supportRelationRepository.deleteAll(expiredRelations);

        eventPublisher.publishEvent(new SupportExpiredEvent(this, expiredRelations));
    }
}
