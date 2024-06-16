/*
 * Created 2018-11-20 12:59:17
 */
package cn.com.yting.kxy.web.impartation;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import cn.com.yting.kxy.core.KxyConstants;
import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceLoader;
import cn.com.yting.kxy.core.util.TimeUtils;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.award.AwardResult;
import cn.com.yting.kxy.web.award.AwardService;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatMessageSentEvent;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.currency.CurrencyChangeLogRepository;
import cn.com.yting.kxy.web.currency.CurrencyChangedEvent;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.equipment.EnhancingResult;
import cn.com.yting.kxy.web.equipment.EquipmentEnhancedEvent;
import cn.com.yting.kxy.web.equipment.EquipmentRepository;
import cn.com.yting.kxy.web.equipment.resource.EquipmentProduce;
import cn.com.yting.kxy.web.equipment.resource.EquipmentStrengtheningStatus;
import cn.com.yting.kxy.web.friend.FriendRepository;
import cn.com.yting.kxy.web.friend.FriendService;
import cn.com.yting.kxy.web.game.goldTower.GoldTowerChallengeEntity;
import cn.com.yting.kxy.web.game.goldTower.GoldTowerChallengeEntityRepository;
import cn.com.yting.kxy.web.game.goldTower.GoldTowerChallengeSuccessEvent;
import cn.com.yting.kxy.web.game.idleMine.IdleMineRepository;
import cn.com.yting.kxy.web.game.kuaibidazhuanpan.KbdzpMadeTurnEvent;
import cn.com.yting.kxy.web.game.minearena.MineArenaChallengeCompletedEvent;
import cn.com.yting.kxy.web.game.minearena.MineArenaChallengeLog;
import cn.com.yting.kxy.web.game.minearena.MineArenaChallengeStartEvent;
import cn.com.yting.kxy.web.game.minearena.MineArenaRecord;
import cn.com.yting.kxy.web.impartation.resource.DailyPracticeAndAchievement;
import cn.com.yting.kxy.web.impartation.resource.DailyPracticeAndAchievementCollectionSupplier;
import cn.com.yting.kxy.web.impartation.resource.ContributionAndExpGetProportion;
import cn.com.yting.kxy.web.invitation.InvitationConstants;
import cn.com.yting.kxy.web.invitation.InvitationRepository;
import cn.com.yting.kxy.web.invitation.InviterRepository;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.market.ConsignmentRepository;
import cn.com.yting.kxy.web.market.GoodsType;
import cn.com.yting.kxy.web.party.PartyMemberInvitedEvent;
import cn.com.yting.kxy.web.party.SupportRelationRepository;
import cn.com.yting.kxy.web.pet.Pet;
import cn.com.yting.kxy.web.pet.PetAbilityAcquiredEvent;
import cn.com.yting.kxy.web.pet.PetEnhanceResult;
import cn.com.yting.kxy.web.pet.PetEnhancedEvent;
import cn.com.yting.kxy.web.pet.PetGachaEvent;
import cn.com.yting.kxy.web.pet.PetRepository;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRelationRepository;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.quest.QuestCompletedEvent;
import cn.com.yting.kxy.web.quest.QuestRecord;
import cn.com.yting.kxy.web.quest.QuestRepository;
import cn.com.yting.kxy.web.quest.model.QuestStatus;
import cn.com.yting.kxy.web.school.SchoolRepository;
import cn.com.yting.kxy.web.game.kuaibidazhuanpan.KbdzpRepository;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class ImpartationService implements ResetTask {

    @Autowired
    private ImpartationRepository impartationRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private DiscipleRepository discipleRepository;
    @Autowired
    private DisciplineRequestRepository disciplineRequestRepository;
    @Autowired
    private DailyPracticeRepository dailyPracticeRepository;
    @Autowired
    private CurrencyChangeLogRepository currencyChangeLogRepository;
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private InviterRepository inviterRepository;
    @Autowired
    private GoldTowerChallengeEntityRepository goldTowerChallengeEntityRepository;
    @Autowired
    private IdleMineRepository idleMineRepository;
    @Autowired
    private PlayerRelationRepository playerRelationRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private SupportRelationRepository supportRelationRepository;
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private QuestRepository questRepository;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private ConsignmentRepository consignmentRepository;
    @Autowired
    private KbdzpRepository kbdzpRepository;

    @Autowired
    private AwardService awardService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MailService mailService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private FriendService friendService;

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ResourceContext resourceContext;

    public ImpartationRecord createOrChangeRole(long accountId, ImpartationRole role) {
        ImpartationRecord record = impartationRepository.findByIdForWrite(accountId).orElseGet(() -> {
            ImpartationRecord r = new ImpartationRecord();
            r.setAccountId(accountId);
            return r;
        });
        Instant currentInstant = timeProvider.currentInstant();
        switch (role) {
            case DISCIPLE:
                if (Duration.between(playerRepository.findById(accountId).get().getCreateTime().toInstant(), currentInstant).compareTo(ImpartationConstants.DURATION_DISCIPLE_LIMIT) > 0) {
                    throw KxyWebException.unknown("角色创建时间不满足条件");
                }
                if (discipleRepository.findByMasterAccountId(accountId).stream()
                        .anyMatch(it -> it.getPhase().equals(DisciplinePhase.PRACTISING))) {
                    throw KxyWebException.unknown("存在未出师的师父方师徒关系");
                }
                break;
            case MASTER:
                Player player = playerRepository.findById(accountId).get();
                if (Duration.between(player.getCreateTime().toInstant(), currentInstant).compareTo(ImpartationConstants.DURATION_DISCIPLE_LIMIT) < 0) {
                    throw KxyWebException.unknown("角色创建时间不满足条件");
                }
                if (player.getPlayerLevel() < ImpartationConstants.LEVEL_MASTER_REQUIREMENT) {
                    throw KxyWebException.unknown("角色等级不满足条件");
                }
                if (player.getFc() < ImpartationConstants.FC_MASTER_REQUIREMENT) {
                    throw KxyWebException.unknown("角色战斗力不满足条件");
                }
                if (discipleRepository.findById(accountId)
                        .map(it -> it.getPhase().equals(DisciplinePhase.PRACTISING))
                        .orElse(false)) {
                    throw KxyWebException.unknown("存在未出师的徒弟方师徒关系");
                }
                break;
            default:
                throw KxyWebException.unknown("无法处理的role： " + role);
        }

        record.setRole(role);
        return impartationRepository.save(record);
    }

    public DisciplineRequest createDisciplineRequest(long accountId, long masterAccountId) {
        DisciplineRequest request = disciplineRequestRepository.findById(accountId, masterAccountId).orElse(null);
        if (request == null) {
            ImpartationRecord record = impartationRepository.findById(accountId).orElseThrow(() -> KxyWebException.notFound("自己的记录不存在"));
            ImpartationRecord masterRecord = impartationRepository.findById(masterAccountId).orElseThrow(() -> KxyWebException.unknown("目标的记录不存在"));
            if (!record.getRole().equals(ImpartationRole.DISCIPLE)) {
                throw KxyWebException.unknown("自己不是徒弟身份");
            }
            if (!masterRecord.getRole().equals(ImpartationRole.MASTER)) {
                throw KxyWebException.unknown("对方不是师父身份");
            }
            if (discipleRepository.existsById(accountId)) {
                throw KxyWebException.unknown("自己已经拜师");
            }
            if (discipleRepository.countByNotEndDisciples(masterAccountId) >= ImpartationConstants.DISCIPLES_COUNT_LIMIT) {
                throw KxyWebException.unknown("目标已达到最大徒弟数");
            }
            request = new DisciplineRequest();
            request.setAccountId(accountId);
            request.setMasterAccountId(masterAccountId);
            request = disciplineRequestRepository.save(request);
        }
        return request;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public DiscipleRecord acceptDisciplineRequest(long accountId, long discipleAccountId) {
        DisciplineRequest request = disciplineRequestRepository.findById(discipleAccountId, accountId).orElseThrow(() -> KxyWebException.notFound("指定的申请不存在"));
        ImpartationRecord record = impartationRepository.findById(accountId).orElseThrow(() -> KxyWebException.notFound("自己的记录不存在"));
        ImpartationRecord discipleImpartationRecord = impartationRepository.findById(discipleAccountId).orElseThrow(() -> KxyWebException.unknown("目标的记录不存在"));
        if (!record.getRole().equals(ImpartationRole.MASTER)) {
            throw ImpartationException.notMaster();
        }
        if (!discipleImpartationRecord.getRole().equals(ImpartationRole.DISCIPLE)) {
            throw ImpartationException.targetNotDisciple();
        }
        if (discipleRepository.existsById(discipleAccountId)) {
            throw ImpartationException.alreadyInDiscipline();
        }
        if (discipleRepository.countByNotEndDisciples(accountId) >= ImpartationConstants.DISCIPLES_COUNT_LIMIT) {
            throw ImpartationException.maxDiscipleCountReached();
        }

        DiscipleRecord discipleRecord = new DiscipleRecord();
        discipleRecord.setAccountId(discipleAccountId);
        discipleRecord.setMasterAccountId(accountId);
        discipleRecord.setPhase(DisciplinePhase.PRACTISING);
        discipleRecord.setCreateDate(new Date(timeProvider.currentTime()));
        discipleRecord.setDeadline(Date.from(timeProvider.currentInstant().plus(ImpartationConstants.DURATION_DISCIPLINE)));
        discipleRecord.setLastContributionExpDelivery(new Date(timeProvider.currentTime()));
        discipleRecord = discipleRepository.saveAndFlush(discipleRecord);
        friendService.become(accountId, discipleAccountId);

        disciplineRequestRepository.delete(request);
        disciplineRequestRepository.flush();
        disciplineRequestRepository.deleteInBulkByAccountId(discipleAccountId);

        return discipleRecord;
    }

    @Override
    public void dailyReset() {
        resourceContext.getLoader(DailyPracticeAndAchievement.class).getAll().values().stream()
                .filter(it -> it.getType() == 2)
                .forEach(it -> {
                    dailyPracticeRepository.resetDailyPracticeStatusByDefinitionId(it.getId());
                });
        discipleRepository.dailyUpdate();
        Date today = Date.from(timeProvider.today().atStartOfDay().toInstant(KxyConstants.KXY_TIME_OFFSET));
        discipleRepository.updateEndPhaseByDeadlineReached(today);
    }

    public List<DailyPracticeRecord> generateDailyPractice(long accountId) {
        DiscipleRecord discipleRecord = discipleRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.unknown("师徒关系记录不存在"));
        if (discipleRecord.isDailyPracticeGenerated()) {
            return dailyPracticeRepository.findByAccountIdAndStatus(accountId, DailyPracticeStatus.IN_PROGRESS);
        }
        if (!discipleRecord.getPhase().equals(DisciplinePhase.PRACTISING)) {
            throw KxyWebException.unknown("当前状态不是修行中");
        }
        discipleRecord.setDailyPracticeGenerated(true);
        ResourceLoader<DailyPracticeAndAchievementCollectionSupplier> loader = resourceContext.getLoader(DailyPracticeAndAchievementCollectionSupplier.class);
        DailyPracticeAndAchievement dailyPracticeAndAchievement;
        DailyPracticeRecord dailyPracticeRecord;
        List<DailyPracticeRecord> list = new ArrayList<>();
        dailyPracticeAndAchievement = loader.get(4479000).get().iterator().next();
        dailyPracticeRecord = dailyPracticeRepository.findOrCreateRecord(accountId, dailyPracticeAndAchievement.getId());
        dailyPracticeRecord.setStatus(DailyPracticeStatus.IN_PROGRESS);
        list.add(dailyPracticeRecord);
        dailyPracticeAndAchievement = loader.get(4479001).get().iterator().next();
        dailyPracticeRecord = dailyPracticeRepository.findOrCreateRecord(accountId, dailyPracticeAndAchievement.getId());
        dailyPracticeRecord.setStatus(DailyPracticeStatus.IN_PROGRESS);
        list.add(dailyPracticeRecord);
        dailyPracticeAndAchievement = loader.get(4479002).get().iterator().next();
        dailyPracticeRecord = dailyPracticeRepository.findOrCreateRecord(accountId, dailyPracticeAndAchievement.getId());
        dailyPracticeRecord.setStatus(DailyPracticeStatus.IN_PROGRESS);
        list.add(dailyPracticeRecord);
        return list;
    }

    public CompleteDailyPracticeResult completeDailyPractice(long accountId, long definitionId) {
        DailyPracticeAndAchievement dailyPracticeAndAchievement = resourceContext.getLoader(DailyPracticeAndAchievement.class).get(definitionId);
        DailyPracticeRecord dailyPracticeRecord;
        if (dailyPracticeAndAchievement.getType() == 1) {
            dailyPracticeRecord = dailyPracticeRepository.findOrCreateRecord(accountId, definitionId);
            if (dailyPracticeRecord.getStatus().equals(DailyPracticeStatus.NOT_STARTED_YET)) {
                dailyPracticeRecord.setStatus(DailyPracticeStatus.IN_PROGRESS);
            }
        } else {
            dailyPracticeRecord = dailyPracticeRepository.findByIdForWrite(accountId, definitionId).orElseThrow(() -> KxyWebException.notFound("日常修行记录不存在"));
        }
        if (!dailyPracticeRecord.getStatus().equals(DailyPracticeStatus.IN_PROGRESS)) {
            throw KxyWebException.unknown("当前的修行状态不是进行中");
        }
        boolean requirementMeeted = false;
        switch ((int) definitionId) {
            case 4470001:
//                requirementMeeted = currencyChangeLogRepository.countByAccountIdAndCurrencyIdAndPurpose(accountId, CurrencyConstants.ID_毫块币, CurrencyConstants.PURPOSE_INCREMENT_块币大转盘) > 0;
                requirementMeeted = kbdzpRepository.getOne(accountId).getTotalTurnCount() >= 1;
                break;
            case 4470002:
//                requirementMeeted = currencyChangeLogRepository.findByAccountIdAndCurrencyIdAndPurpose(accountId, CurrencyConstants.ID_毫块币, CurrencyConstants.PURPOSE_INCREMENT_块币大转盘).stream()
//                        .anyMatch(it -> it.getAfterAmount() - it.getBeforeAmount() == 10_000);
                requirementMeeted = kbdzpRepository.getOne(accountId).getTotalTurnCount() >= 10;
                break;
            case 4470003:
                requirementMeeted = invitationRepository.countByInviterIdAndInviterDepth(accountId, InvitationConstants.DIRECT_INVITATION_DEPTH) >= 1;
                break;
            case 4470004:
                requirementMeeted = invitationRepository.countByInviterIdAndInviterDepth(accountId, InvitationConstants.DIRECT_INVITATION_DEPTH) >= 2;
                break;
            case 4470005:
                requirementMeeted = invitationRepository.countByInviterIdAndInviterDepth(accountId, InvitationConstants.DIRECT_INVITATION_DEPTH) >= 3;
                break;
            case 4470006:
                requirementMeeted = invitationRepository.countByInviterIdAndInviterDepth(accountId, InvitationConstants.DIRECT_INVITATION_DEPTH) >= 5;
                break;
            case 4470007:
                requirementMeeted = inviterRepository.findById(accountId).get().getInvitationLimit() >= InvitationConstants.DEFAULT_INVITATION_LIMIT + 1;
                break;
            case 4470008:
                requirementMeeted = currencyChangeLogRepository.countByAccountIdAndCurrencyIdAndPurpose(accountId, CurrencyConstants.ID_毫仙石, CurrencyConstants.PURPOSE_INCREMENT_邀请回报) > 0;
                break;
            case 4470009:
                requirementMeeted = currencyChangeLogRepository.countByAccountIdAndPurpose(accountId, CurrencyConstants.PURPOSE_DECREMENT_获得宠物) >= 1;
                break;
            case 4470010:
                requirementMeeted = currencyChangeLogRepository.countByAccountIdAndPurpose(accountId, CurrencyConstants.PURPOSE_DECREMENT_获得宠物) >= 3;
                break;
            case 4470011:
                requirementMeeted = currencyChangeLogRepository.countByAccountIdAndPurpose(accountId, CurrencyConstants.PURPOSE_DECREMENT_获得宠物) >= 10;
                break;
            case 4470012:
                requirementMeeted = goldTowerChallengeEntityRepository.findByAccountId(accountId).getLastFloorCount() >= 20;
                break;
            case 4470013:
                requirementMeeted = goldTowerChallengeEntityRepository.findByAccountId(accountId).getLastFloorCount() >= 50;
                break;
            case 4470014:
                requirementMeeted = currencyChangeLogRepository.countByAccountIdAndCurrencyIdAndPurpose(accountId, CurrencyConstants.ID_元宝, CurrencyConstants.PURPOSE_INCREMENT_抢占摇钱树产出) > 0;
                break;
            case 4470015:
                requirementMeeted = currencyChangeLogRepository.sumConsumptionByAccountIdAndCurrencyId(accountId, CurrencyConstants.ID_藏宝图) >= 1;
                break;
            case 4470016:
                requirementMeeted = currencyChangeLogRepository.sumConsumptionByAccountIdAndCurrencyId(accountId, CurrencyConstants.ID_藏宝图) >= 3;
                break;
            case 4470017:
                requirementMeeted = currencyChangeLogRepository.sumConsumptionByAccountIdAndCurrencyId(accountId, CurrencyConstants.ID_藏宝图) >= 10;
                break;
            case 4470018:
                requirementMeeted = currencyChangeLogRepository.sumConsumptionByAccountIdAndCurrencyId(accountId, CurrencyConstants.ID_藏宝图) >= 20;
                break;
            case 4470019:
                requirementMeeted = idleMineRepository.findByAccountId(accountId).getAvailableMineQueueCount() > 1;
                break;
            case 4470020:
                requirementMeeted = currencyChangeLogRepository.countByAccountIdAndPurpose(accountId, CurrencyConstants.PURPOSE_DECREMENT_三界经商雇佣商队) > 0;
                break;
            case 4470021:
                requirementMeeted = playerRelationRepository.findById(accountId).get().toEquipmentIds().stream()
                        .allMatch(id -> Optional.ofNullable(id)
                        .flatMap(equipmentRepository::findById)
                        .map(it -> resourceContext.getLoader(EquipmentProduce.class).get(it.getDefinitionId()).getColor() >= 2)
                        .orElse(false)
                        );
                break;
            case 4470022:
                requirementMeeted = playerRelationRepository.findById(accountId).get().toEquipmentIds().stream()
                        .allMatch(id -> Optional.ofNullable(id)
                        .flatMap(equipmentRepository::findById)
                        .map(it -> resourceContext.getLoader(EquipmentProduce.class).get(it.getDefinitionId()).getColor() >= 3)
                        .orElse(false)
                        );
                break;
            case 4470023:
                requirementMeeted = playerRelationRepository.findById(accountId).get().toEquipmentIds().stream()
                        .allMatch(id -> Optional.ofNullable(id)
                        .flatMap(equipmentRepository::findById)
                        .map(it -> resourceContext.getLoader(EquipmentProduce.class).get(it.getDefinitionId()).getColor() >= 4)
                        .orElse(false)
                        );
                break;
            case 4470024:
                requirementMeeted = playerRelationRepository.findById(accountId).get().toEquipmentIds().stream()
                        .allMatch(id -> Optional.ofNullable(id)
                        .flatMap(equipmentRepository::findById)
                        .map(it -> resourceContext.getLoader(EquipmentProduce.class).get(it.getDefinitionId()).getColor() >= 5)
                        .orElse(false)
                        );
                break;
            case 4470025:
                requirementMeeted = currencyChangeLogRepository.countByAccountIdAndPurpose(accountId, CurrencyConstants.PURPOSE_DECREMENT_装备打造) > 0;
                break;
            case 4470026:
                requirementMeeted = currencyChangeLogRepository.countByAccountIdAndPurpose(accountId, CurrencyConstants.PURPOSE_DECREMENT_装备打造) >= 6;
                break;
            case 4470027:
                requirementMeeted = currencyChangeLogRepository.countByAccountIdAndPurpose(accountId, CurrencyConstants.PURPOSE_DECREMENT_装备打造) >= 30;
                break;
            case 4470028:
                requirementMeeted = equipmentRepository.findByAccountId(accountId).stream()
                        .anyMatch(it -> it.getEnhanceLevel() >= 4);
                break;
            case 4470029:
                requirementMeeted = equipmentRepository.findByAccountId(accountId).stream()
                        .anyMatch(it -> it.getEnhanceLevel() >= 7);
                break;
            case 4470030:
                requirementMeeted = petRepository.findByAccountId(accountId).stream()
                        .anyMatch(it -> it.getRank() < 4 && it.getAbilities().size() >= 5 || it.getAbilities().size() >= 6);
                break;
            case 4470031:
                requirementMeeted = petRepository.findByAccountId(accountId).stream()
                        .anyMatch(it -> it.getRank() < 4 && it.getAbilities().size() >= 10 || it.getAbilities().size() >= 11);
                break;
            case 4470032:
                requirementMeeted = petRepository.findByAccountId(accountId).stream()
                        .anyMatch(it -> it.getRank() >= 4);
                break;
            case 4470033:
                requirementMeeted = petRepository.findByAccountId(accountId).stream()
                        .anyMatch(it -> it.getRank() >= 7);
                break;
            case 4470034:
                requirementMeeted = supportRelationRepository.findPartyMembers(accountId).size() > 0;
                break;
            case 4470035:
                requirementMeeted = schoolRepository.findById(accountId).get().getAblitiesLevelList().stream()
                        .allMatch(it -> it >= 15);
                break;
            case 4470036:
                requirementMeeted = schoolRepository.findById(accountId).get().getAblitiesLevelList().stream()
                        .allMatch(it -> it >= 25);
                break;
            case 4470037:
                requirementMeeted = schoolRepository.findById(accountId).get().getAblitiesLevelList().stream()
                        .allMatch(it -> it >= 35);
                break;
            case 4470038:
                requirementMeeted = schoolRepository.findById(accountId).get().getAblitiesLevelList().stream()
                        .allMatch(it -> it >= 45);
                break;
            case 4470039:
                requirementMeeted = schoolRepository.findById(accountId).get().getAblitiesLevelList().stream()
                        .allMatch(it -> it >= 55);
                break;
            case 4470040:
                requirementMeeted = schoolRepository.findById(accountId).get().getAblitiesLevelList().stream()
                        .allMatch(it -> it >= 65);
                break;
            case 4470041:
                requirementMeeted = consignmentRepository.countBySellerAccountIdAndSoldIsTrue(accountId) > 0;
                break;
            case 4470042:
                requirementMeeted = consignmentRepository.countBySellerAccountIdAndGoodsType(accountId, GoodsType.PET) > 0;
                break;
            case 4470043:
                requirementMeeted = consignmentRepository.countBySellerAccountIdAndGoodsType(accountId, GoodsType.EQUIPMENT) > 0;
                break;
            case 4470044:
                requirementMeeted = consignmentRepository.countBySellerAccountIdAndSoldIsTrue(accountId) >= 5;
                break;
            case 4470045:
                requirementMeeted = consignmentRepository.countByBuyerAccountIdAndGoodsType(accountId, GoodsType.PET) > 0;
                break;
            case 4470046:
                requirementMeeted = consignmentRepository.countByBuyerAccountIdAndGoodsType(accountId, GoodsType.EQUIPMENT) > 0;
                break;
            case 4470047:
                requirementMeeted = questRepository.findById(accountId, 700009)
                        .map(it -> it.getQuestStatus().equals(QuestStatus.COMPLETED))
                        .orElse(false);
                break;
            case 4470048:
                requirementMeeted = questRepository.findById(accountId, 700040)
                        .map(it -> it.getQuestStatus().equals(QuestStatus.COMPLETED))
                        .orElse(false);
                break;
            case 4470049:
                requirementMeeted = questRepository.findById(accountId, 700056)
                        .map(it -> it.getQuestStatus().equals(QuestStatus.COMPLETED))
                        .orElse(false);
                break;
            case 4470050:
                requirementMeeted = questRepository.findById(accountId, 700071)
                        .map(it -> it.getQuestStatus().equals(QuestStatus.COMPLETED))
                        .orElse(false);
                break;
            case 4470051:
                requirementMeeted = questRepository.findById(accountId, 700088)
                        .map(it -> it.getQuestStatus().equals(QuestStatus.COMPLETED))
                        .orElse(false);
                break;
            case 4470052:
                requirementMeeted = questRepository.findById(accountId, 700104)
                        .map(it -> it.getQuestStatus().equals(QuestStatus.COMPLETED))
                        .orElse(false);
                break;
            case 4470053:
                requirementMeeted = questRepository.findById(accountId, 710115)
                        .map(it -> it.getQuestStatus().equals(QuestStatus.COMPLETED))
                        .orElse(false);
                break;
            case 4470054:
                requirementMeeted = questRepository.findById(accountId, 710089)
                        .map(it -> it.getQuestStatus().equals(QuestStatus.COMPLETED))
                        .orElse(false);
                break;
            case 4470055:
                requirementMeeted = questRepository.findById(accountId, 710056)
                        .map(it -> it.getQuestStatus().equals(QuestStatus.COMPLETED))
                        .orElse(false)
                        || questRepository.findById(accountId, 710064)
                                .map(it -> it.getQuestStatus().equals(QuestStatus.COMPLETED))
                                .orElse(false);
                break;
            case 4470056:
                requirementMeeted = questRepository.findById(accountId, 710029)
                        .map(it -> it.getQuestStatus().equals(QuestStatus.COMPLETED))
                        .orElse(false)
                        || questRepository.findById(accountId, 710034)
                                .map(it -> it.getQuestStatus().equals(QuestStatus.COMPLETED))
                                .orElse(false);
                break;
            case 4470057:
                requirementMeeted = friendRepository.findByAccountId(accountId).getFriends().size() >= 3;
                break;
            case 4470058:
                requirementMeeted = friendRepository.findByAccountId(accountId).getFriends().size() >= 10;
                break;
            case 4470059:
                requirementMeeted = friendRepository.findByAccountId(accountId).getFriends().size() >= 20;
                break;
        }
        if (requirementMeeted) {
            dailyPracticeRecord.setStatus(DailyPracticeStatus.COMPLETED);
        }
        return new CompleteDailyPracticeResult(dailyPracticeRecord, requirementMeeted);
    }

    private void makeDailyPracticeProgress(long accountId, long definitionId, int goal) {
        makeDailyPracticeProgress(accountId, definitionId, goal, null);
    }

    private void makeDailyPracticeProgress(long accountId, long definitionId, int goal, Integer providedProgress) {
        DailyPracticeRecord dailyPracticeRecord = dailyPracticeRepository.findByIdForWrite(accountId, definitionId).orElse(null);
        if (dailyPracticeRecord != null && dailyPracticeRecord.getStatus().equals(DailyPracticeStatus.IN_PROGRESS)) {
            if (providedProgress == null) {
                dailyPracticeRecord.increaseProgress();
            } else {
                dailyPracticeRecord.setProgress(providedProgress);
            }
            if (dailyPracticeRecord.getProgress() >= goal) {
                dailyPracticeRecord.setStatus(DailyPracticeStatus.COMPLETED);
            }
        }
    }

    public ObtainDailyPracticeRewardResult obtainDailyPracticeReward(long accountId, long definitionId) {
        DailyPracticeRecord dailyPracticeRecord = dailyPracticeRepository.findByIdForWrite(accountId, definitionId).orElseThrow(() -> KxyWebException.notFound("日常修行记录不存在"));
        if (!dailyPracticeRecord.getStatus().equals(DailyPracticeStatus.COMPLETED)) {
            throw KxyWebException.unknown("当前的修行状态不是已完成");
        }
        DailyPracticeAndAchievement dailyPracticeAndAchievement = resourceContext.getLoader(DailyPracticeAndAchievement.class).get(dailyPracticeRecord.getDefinitionId());
        AwardResult awardResult = awardService.processAward(accountId, dailyPracticeAndAchievement.getAward());
        dailyPracticeRecord.setStatus(DailyPracticeStatus.REWARDED);
        return new ObtainDailyPracticeRewardResult(dailyPracticeRecord, awardResult);
    }

    public DiscipleRecord confirmDisciplineEndAsDisciple(long accountId) {
        DiscipleRecord discipleRecord = discipleRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.unknown("师徒关系记录不存在"));
        if (!discipleRecord.getPhase().equals(DisciplinePhase.TO_BE_CONFIRMED)) {
            throw KxyWebException.unknown("当前状态不是已出师");
        }
        if (discipleRecord.isDiscipleConfirmed()) {
            throw KxyWebException.unknown("当前已经确认过");
        }
        discipleRecord.setDiscipleConfirmed(true);
        checkConfirmationAndSetHuoyuePool(discipleRecord);
        return discipleRecord;
    }

    public DiscipleRecord confirmDisciplineEndAsMaster(long accountId, long discipleAccountId) {
        DiscipleRecord discipleRecord = discipleRepository.findByIdForWrite(discipleAccountId).orElseThrow(() -> KxyWebException.unknown("师徒关系记录不存在"));
        if (!discipleRecord.getPhase().equals(DisciplinePhase.TO_BE_CONFIRMED)) {
            throw KxyWebException.unknown("当前状态不是已出师");
        }
        if (discipleRecord.getMasterAccountId() != accountId) {
            throw KxyWebException.unknown("不是自己的徒弟");
        }
        if (discipleRecord.isMasterConfirmed()) {
            throw KxyWebException.unknown("当前已经确认过");
        }
        discipleRecord.setMasterConfirmed(true);
        checkConfirmationAndSetHuoyuePool(discipleRecord);
        return discipleRecord;
    }

    private void checkConfirmationAndSetHuoyuePool(DiscipleRecord discipleRecord) {
        if (discipleRecord.isDiscipleConfirmed() && discipleRecord.isMasterConfirmed()) {
            discipleRecord.setHuoyuePool(currencyService.findOrCreateRecord(discipleRecord.getAccountId(), CurrencyConstants.ID_师徒值).getAmount());
            discipleRecord.setConfirmationDate(new Date(timeProvider.currentTime()));
            discipleRecord.setPhase(DisciplinePhase.END);
            //
            List<CurrencyStack> stacks;
            long huoyueToDisciple = (long) (discipleRecord.getHuoyuePool() * ImpartationConstants.RATE_活跃点_AWARD_ON_CONFIRMATION_DISCIPLE);
            stacks = Collections.singletonList(new CurrencyStack(CurrencyConstants.ID_活跃点, Math.max(huoyueToDisciple, 1)));
            MailSendingRequest.create()
                    .to(discipleRecord.getAccountId())
                    .template(ImpartationConstants.MAIL_ID_CONFIRMATION)
                    .attachment(stacks)
                    .commit(mailService);
            //
            long huoyueToMaster = (long) (discipleRecord.getHuoyuePool() * ImpartationConstants.RATE_活跃点_AWARD_ON_CONFIRMATION_MASTER);
            stacks = Collections.singletonList(new CurrencyStack(CurrencyConstants.ID_活跃点, Math.max(huoyueToMaster, 1)));
            MailSendingRequest.create()
                    .to(discipleRecord.getMasterAccountId())
                    .template(ImpartationConstants.MAIL_ID_CONFIRMATION)
                    .attachment(stacks)
                    .commit(mailService);
            chatService.sendSystemMessage(
                    ChatConstants.SERVICE_ID_UNDIFINED,
                    ChatMessage.createTemplateMessage(
                            ImpartationConstants.BROADCAST_ID_CONFIRMATION,
                            ImmutableMap.of(
                                    "studentName", playerRepository.findById(discipleRecord.getAccountId()).get().getPlayerName(),
                                    "teacherName", playerRepository.findById(discipleRecord.getMasterAccountId()).get().getPlayerName()
                            )
                    )
            );
        }
    }

    public long calculateCurrentHuoyuePool(long accountId) {
        DiscipleRecord discipleRecord = discipleRepository.findById(accountId).orElseThrow(() -> KxyWebException.unknown("师徒关系记录不存在"));
        return currencyService.findOrCreateRecord(discipleRecord.getAccountId(), CurrencyConstants.ID_师徒值).getAmount();
    }

    public CurrencyStack obtainHuoyuePoolAwardAsDisciple(long accountId) {
        DiscipleRecord discipleRecord = discipleRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.unknown("师徒关系记录不存在"));
        if (!discipleRecord.getPhase().equals(DisciplinePhase.END)) {
            throw KxyWebException.unknown("当前状态不是已确认");
        }
        LocalDate today = timeProvider.today();
        LocalDate deadlineDate = ((java.sql.Date) discipleRecord.getConfirmationDate()).toLocalDate();
        if (!deadlineDate.plusDays(ImpartationConstants.DAYS_活跃点_AWARD_AVAILABLE_DISCIPLE).isAfter(today)) {
            throw KxyWebException.unknown("当前已经超过能够领取的时间");
        }
        if (discipleRecord.getDiscipleLastHuoyueDelivery() != null && !TimeUtils.toOffsetTime(discipleRecord.getDiscipleLastHuoyueDelivery()).toLocalDate().isBefore(today)) {
            throw ImpartationException.当前未到能够领取的时间();
        }
        CurrencyStack stack = new CurrencyStack(CurrencyConstants.ID_活跃点, Math.max((long) (discipleRecord.getHuoyuePool() * 0.1), 1));
        currencyService.increaseCurrency(accountId, stack.getCurrencyId(), stack.getAmount(), CurrencyConstants.PURPOSE_INCREMENT_师徒系统分红);
        discipleRecord.setDiscipleLastHuoyueDelivery(new Date(timeProvider.currentTime()));
        return stack;
    }

    public CurrencyStack obtainHuoyuePoolAwardAsMaster(long accountId, long discipleAccountId) {
        DiscipleRecord discipleRecord = discipleRepository.findByIdForWrite(discipleAccountId).orElseThrow(() -> KxyWebException.unknown("师徒关系记录不存在"));
        if (!discipleRecord.getPhase().equals(DisciplinePhase.END)) {
            throw KxyWebException.unknown("当前状态不是已确认");
        }
        LocalDate today = timeProvider.today();
        LocalDate deadlineDate = ((java.sql.Date) discipleRecord.getConfirmationDate()).toLocalDate();
        if (discipleRecord.getMasterAccountId() != accountId) {
            throw KxyWebException.unknown("不是自己的徒弟");
        }
        if (!deadlineDate.plusDays(ImpartationConstants.DAYS_活跃点_AWARD_AVAILABLE_MASTER).isAfter(today)) {
            throw KxyWebException.unknown("当前已经超过能够领取的时间");
        }
        if (discipleRecord.getMasterLastHuoyueDelivery() != null && !TimeUtils.toOffsetTime(discipleRecord.getMasterLastHuoyueDelivery()).toLocalDate().isBefore(today)) {
            throw ImpartationException.当前未到能够领取的时间();
        }
        CurrencyStack stack = new CurrencyStack(CurrencyConstants.ID_活跃点, Math.max((long) (discipleRecord.getHuoyuePool() * 0.1), 1));
        currencyService.increaseCurrency(accountId, stack.getCurrencyId(), stack.getAmount(), CurrencyConstants.PURPOSE_INCREMENT_师徒系统分红);
        discipleRecord.setMasterLastHuoyueDelivery(new Date(timeProvider.currentTime()));
        return stack;
    }

    public void deleteDiscipleReocrdAsDisciple(long accountId) {
        DiscipleRecord discipleRecord = discipleRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.unknown("师徒关系记录不存在"));
        if (discipleRecord.getPhase().equals(DisciplinePhase.END)) {
            throw KxyWebException.unknown("当前状态是已出师");
        }
        LocalDate masterLastLoginDate = TimeUtils.toOffsetTime(playerRepository.findById(discipleRecord.getMasterAccountId()).get().getLastLoginTime()).toLocalDate();
        if (masterLastLoginDate.plus(ImpartationConstants.PERIOD_LAST_LOGIN_TO_DELETE).isAfter(timeProvider.today())) {
            throw ImpartationException.masterNotIdle();
        }
        discipleRepository.delete(discipleRecord);
        dailyPracticeRepository.deleteByAccountId(accountId);
    }

    public void deleteDiscipleReocrdAsMaster(long accountId, long discipleAccountId) {
        DiscipleRecord discipleRecord = discipleRepository.findByIdForWrite(discipleAccountId).orElseThrow(() -> KxyWebException.unknown("师徒关系记录不存在"));
        if (discipleRecord.getMasterAccountId() != accountId) {
            throw KxyWebException.unknown("不是自己的徒弟");
        }
        if (discipleRecord.getPhase().equals(DisciplinePhase.END)) {
            throw KxyWebException.unknown("当前状态是已出师");
        }
        LocalDate discipleLastLoginDate = TimeUtils.toOffsetTime(playerRepository.findById(discipleAccountId).get().getLastLoginTime()).toLocalDate();
        if (discipleLastLoginDate.plus(ImpartationConstants.PERIOD_LAST_LOGIN_TO_DELETE).isAfter(timeProvider.today())) {
            throw ImpartationException.discipleNotIdle();
        }
        discipleRepository.delete(discipleRecord);
        dailyPracticeRepository.deleteByAccountId(discipleAccountId);
    }

    @EventListener
    public void onQuestCompleted(QuestCompletedEvent event) {
        QuestRecord questRecord = event.getQuestRecord();
        if (questRecord.getQuestId() >= 720005 && questRecord.getQuestId() <= 720022) {
            discipleRepository.findByMasterAccountIdForWrite(questRecord.getAccountId()).stream()
                    .filter(it -> it.getPhase().equals(DisciplinePhase.PRACTISING))
                    .forEach(it -> {
                        it.increaseTodayContributionPool(500);
                        it.increaseTodayExpPool(35000);
                    });
        } else if (questRecord.getQuestId() >= 720028 && questRecord.getQuestId() <= 720033) {
            discipleRepository.findByMasterAccountId(questRecord.getAccountId()).stream()
                    .filter(it -> it.getPhase().equals(DisciplinePhase.PRACTISING))
                    .forEach(it -> {
                        it.increaseTodayContributionPool(390);
                        it.increaseTodayExpPool(70000);
                    });
        }
        if (questRecord.getQuestId() == 720033) {
            makeDailyPracticeProgress(questRecord.getAccountId(), 4478005, 1);
        }
    }

    @EventListener
    public void onMineArenaChallengeStart(MineArenaChallengeStartEvent event) {
        MineArenaRecord mineArenaRecord = event.getMineArenaRecord();
        discipleRepository.findByMasterAccountIdForWrite(mineArenaRecord.getAccountId()).stream()
                .filter(it -> it.getPhase().equals(DisciplinePhase.PRACTISING))
                .forEach(it -> {
                    it.increaseTodayContributionPool(330);
                    it.increaseTodayExpPool(60160);
                });
    }

    @EventListener
    public void onGoldTowerChallengeSuccess(GoldTowerChallengeSuccessEvent event) {
        GoldTowerChallengeEntity goldTowerChallengeEntity = event.getGoldTowerChallengeEntity();
        discipleRepository.findByMasterAccountIdForWrite(goldTowerChallengeEntity.getAccountId()).stream()
                .filter(it -> it.getPhase().equals(DisciplinePhase.PRACTISING))
                .forEach(it -> {
                    it.increaseTodayContributionPool(220);
                    it.increaseTodayExpPool(55000);
                });
        makeDailyPracticeProgress(goldTowerChallengeEntity.getAccountId(), 4478003, 30, (int) goldTowerChallengeEntity.getLastFloorCount());
    }

    public List<CurrencyStack> obtainContributionExpPoolAward(long accountId) {
        DiscipleRecord discipleRecord = discipleRepository.findByIdForWrite(accountId).orElseThrow(() -> KxyWebException.unknown("师徒关系记录不存在"));
        if (!TimeUtils.toOffsetTime(discipleRecord.getLastContributionExpDelivery()).toLocalDate().isBefore(timeProvider.today())) {
            throw ImpartationException.当前未到能够领取的时间();
        }
        if (!discipleRecord.getPhase().equals(DisciplinePhase.PRACTISING)) {
            throw KxyWebException.unknown("当前状态不是修行中");
        }
        ContributionAndExpGetProportion contributionAndExpGetProportion = resourceContext.getLoader(ContributionAndExpGetProportion.class).get(discipleRecord.getPlayerLevelAtMidnight());
        List<CurrencyStack> list = new ArrayList<>();
        list.add(new CurrencyStack(CurrencyConstants.ID_门贡, (long) (discipleRecord.getYesterdayContributionPool() * contributionAndExpGetProportion.getContributionProportion())));
        list.add(new CurrencyStack(CurrencyConstants.ID_经验, (long) (discipleRecord.getYesterdayExpPool() * contributionAndExpGetProportion.getExpProportion())));
        list.forEach(it -> currencyService.increaseCurrency(accountId, it.getCurrencyId(), it.getAmount()));
        discipleRecord.setLastContributionExpDelivery(new Date(timeProvider.currentTime()));
        return list;
    }

    @EventListener
    public void onKbdzpMadeTurn(KbdzpMadeTurnEvent event) {
        makeDailyPracticeProgress(event.getKbdzpRecord().getAccountId(), 4478001, 10);
    }

    @EventListener
    public void onPetGacha(PetGachaEvent event) {
        makeDailyPracticeProgress(event.getPet().getAccountId(), 4478002, 1);
    }

    @EventListener
    public void onMineArenaChallengeCompleted(MineArenaChallengeCompletedEvent event) {
        MineArenaChallengeLog mineArenaChallengeLog = event.getMineArenaChallengeLog();
        if (mineArenaChallengeLog.isSuccess()) {
            makeDailyPracticeProgress(mineArenaChallengeLog.getChallengerAccountId(), 4478004, 1);
        }
    }

    @EventListener
    public void onCurrencyChanged(CurrencyChangedEvent event) {
        if (event.getPurpose() != null) {
            switch (event.getPurpose()) {
                case CurrencyConstants.PURPOSE_DECREMENT_三界经商雇佣商队:
                    makeDailyPracticeProgress(event.getAccountId(), 4478006, 2);
                    break;
                case CurrencyConstants.PURPOSE_DECREMENT_装备打造:
                    makeDailyPracticeProgress(event.getAccountId(), 4478007, 1);
                    break;
            }
        }
    }

    @EventListener
    public void onEquipmentEnhanced(EquipmentEnhancedEvent event) {
        EnhancingResult result = event.getResult();
        if (result.getStatus().equals(EquipmentStrengtheningStatus.SUCCESSFUL)) {
            makeDailyPracticeProgress(result.getEquipmentDetail().getEquipment().getAccountId(), 4478008, 1);
        }
    }

    @EventListener
    public void onPetEnhanced(PetEnhancedEvent event) {
        PetEnhanceResult result = event.getResult();
        if (result.isSuccess()) {
            makeDailyPracticeProgress(result.getPet().getAccountId(), 4478010, 1);
        }
    }

    @EventListener
    public void onPetAbilityAcquired(PetAbilityAcquiredEvent event) {
        Pet pet = event.getPet();
        makeDailyPracticeProgress(pet.getAccountId(), 4478009, 1);
    }

    @EventListener
    public void onPartyMemberInvitedEvent(PartyMemberInvitedEvent event) {
        if (supportRelationRepository.findPartyMembers(event.getAccountId()).size() == 2) {
            makeDailyPracticeProgress(event.getAccountId(), 4478011, 1);
        }
    }

    @EventListener
    public void onChatMessageSentEvent(ChatMessageSentEvent event) {
        ChatMessage chatMessage = event.getChatMessage();
        if (!chatMessage.isSystemMessage() && !chatMessage.isBroadcast()) {
            makeDailyPracticeProgress(chatMessage.getSenderId(), 4478012, 1);
        }
    }

}
