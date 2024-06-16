/*
 * Created 2018-11-12 18:51:48
 */
package cn.com.yting.kxy.web.auction;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.random.RandomSelectType;
import cn.com.yting.kxy.core.random.RandomSelector;
import cn.com.yting.kxy.core.random.RandomSelectorBuilder;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.auction.resource.BlackMarketAuctionInfo;
import cn.com.yting.kxy.web.chat.ChatConstants;
import cn.com.yting.kxy.web.chat.ChatService;
import cn.com.yting.kxy.web.chat.model.ChatMessage;
import cn.com.yting.kxy.web.chat.model.TemplateTypes;
import cn.com.yting.kxy.web.controller.ControllerUtils;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.currency.resource.Currency;
import cn.com.yting.kxy.web.equipment.Equipment;
import cn.com.yting.kxy.web.equipment.EquipmentService;
import cn.com.yting.kxy.web.equipment.resource.GodEquipment;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.pet.Pet;
import cn.com.yting.kxy.web.pet.PetService;
import cn.com.yting.kxy.web.pet.resource.GodPet;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.title.Title;
import cn.com.yting.kxy.web.title.TitleService;
import cn.com.yting.kxy.web.title.resource.GodTitle;
import cn.com.yting.kxy.web.title.resource.TitleInformations;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class AuctionService implements ResetTask {

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private AuctionSharedRepository auctionSharedRepository;
    @Autowired
    private CommodityRepository commodityRepository;
    @Autowired
    private CommodityPlayerRepository commodityPlayerRepository;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private MailService mailService;
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private PetService petService;
    @Autowired
    private TitleService titleService;

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private ResourceContext resourceContext;

    RandomSelector<Long> jackpot;

    public void init() {
        auctionSharedRepository.init(new AuctionSharedRecord());

        AuctionSharedRecord sharedRecord = auctionSharedRepository.getTheRecordForWrite();
        if (sharedRecord.getPausedTime() != null) {
            Instant currentInstant = timeProvider.currentInstant();
            Duration diffDuration = Duration.between(sharedRecord.getPausedTime().toInstant(), currentInstant);
            commodityRepository.findByCommodityStatus(CommodityStatus.ON_SALE).forEach(commodity -> {
                Instant deadlineInstant = commodity.getDeadline().toInstant();
                deadlineInstant = deadlineInstant.plus(diffDuration);
                if (Duration.between(currentInstant, deadlineInstant).compareTo(AuctionConstants.DURATION_RESUME_NEED_EXTRA_TIME) < 0) {
                    deadlineInstant = currentInstant.plus(AuctionConstants.DURATION_RESUME_EXTRA_TIME);
                }
                commodity.setDeadline(Date.from(deadlineInstant));
            });
            sharedRecord.setPausedTime(null);
        }
        //
        RandomSelectorBuilder jackpotBuilder = RandomSelector.<Long>builder();
        List<BlackMarketAuctionInfo> jackpots = new ArrayList<>(resourceContext.getLoader(BlackMarketAuctionInfo.class).getAll().values());
        jackpots.forEach((ssj) -> {
            jackpotBuilder.add(ssj.getId(), ssj.getProbability());
        });
        jackpot = jackpotBuilder.build(RandomSelectType.DEPENDENT);
    }

    public void destroy() {
        AuctionSharedRecord sharedRecord = auctionSharedRepository.getTheRecordForWrite();
        sharedRecord.setPausedTime(new Date(timeProvider.currentTime()));
    }

    public void putOnSale() {
        int onSaleCount = commodityRepository.findByCommodityStatus(CommodityStatus.ON_SALE).size();
        if (onSaleCount >= AuctionConstants.COUNT_MAX_ON_SALE) {
            return;
        }
        List<Commodity> randomCommodities = getRandomCommodities(AuctionConstants.COUNT_MAX_ON_SALE - onSaleCount);
        Iterator<Commodity> iter = randomCommodities.iterator();
        Instant currentInstant = timeProvider.currentInstant();
        while (iter.hasNext()) {
            Commodity commodity = iter.next();

            BlackMarketAuctionInfo blackMarketAuctionInfo = resourceContext.getLoader(BlackMarketAuctionInfo.class).get(commodity.getDefinitionId());
            commodity.setLastBid(blackMarketAuctionInfo.getFloorPrice());
            commodity.setCommodityStatus(CommodityStatus.ON_SALE);
            commodity.setDeadline(Date.from(currentInstant.plusSeconds(blackMarketAuctionInfo.getTime())));
            onSaleCount++;
            if (onSaleCount >= AuctionConstants.COUNT_MAX_ON_SALE) {
                return;
            }
        }
    }

    private List<Commodity> getRandomCommodities(int count) {
        List<Commodity> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            long jackpotId = jackpot.getSingle();
            BlackMarketAuctionInfo blackMarketAuctionInfo = resourceContext.getLoader(BlackMarketAuctionInfo.class).get(jackpotId);
            Commodity commodity = new Commodity();
            commodity.setDefinitionId(blackMarketAuctionInfo.getId());
            commodity.setCommodityStatus(CommodityStatus.QUEUING);
            commodity = commodityRepository.saveAndFlush(commodity);
            result.add(commodity);
        }
        return result;
    }

    public AuctionRecord createRecord(long accountId) {
        if (playerRepository.findById(accountId).map(it -> it.getPlayerLevel()).orElse(0) < AuctionConstants.PLAYER_LEVEL_LIMIT) {
            throw AuctionException.levelNotMeetRequirement();
        }
        if (auctionRepository.existsById(accountId)) {
            throw KxyWebException.unknown("记录已经存在");
        }
        AuctionRecord record = new AuctionRecord();
        record.setAccountId(accountId);
        record.setLikedTodayLimit(AuctionConstants.dailyLikeUpperLimit(playerRepository, accountId));
        return auctionRepository.save(record);
    }

    public AuctionOverall getOverall(long accountId) {
        AuctionRecord auctionRecord = auctionRepository.findById(accountId).orElseThrow(() -> ControllerUtils.notFoundException());
        List<CommodityDetail> onSaleCommodities = commodityRepository.findByCommodityStatus(CommodityStatus.ON_SALE).stream()
                .map(it -> it.toDetail(commodityPlayerRepository))
                .collect(Collectors.toList());
        Set<Long> onSaleCommodityIds = onSaleCommodities.stream()
                .map(it -> it.getCommodity().getId())
                .collect(Collectors.toSet());
        List<CommodityPlayerRecord> commodityPlayerRecords = commodityPlayerRepository.findByAccountId(accountId).stream()
                .filter(it -> onSaleCommodityIds.contains(it.getCommodityId()))
                .collect(Collectors.toList());
        return new AuctionOverall(auctionRecord, onSaleCommodities, commodityPlayerRecords);
    }

    public Commodity bid(long accountId, long commodityId, long price) {
        AuctionRecord record = auctionRepository.findByIdForWrite(accountId).get();
        Commodity commodity = commodityRepository.findByIdForWrite(commodityId).orElseThrow(() -> KxyWebException.notFound("商品不存在"));
        long currentTime = timeProvider.currentTime();
        if (!commodity.getCommodityStatus().equals(CommodityStatus.ON_SALE)) {
            throw AuctionException.commodityNotOnSale();
        }
        if (commodity.getDeadline().getTime() < currentTime) {
            throw AuctionException.saleTimeUp();
        }
        if (Objects.equals(commodity.getLastBidderAccountId(), accountId)) {
            throw AuctionException.noRepeatBid();
        }
        if (currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_元宝).getAmount() + record.getStockYb() < price) {
            throw AuctionException.insufficientCurrency();
        }
        BlackMarketAuctionInfo blackMarketAuctionInfo = resourceContext.getLoader(BlackMarketAuctionInfo.class).get(commodity.getDefinitionId());
        long lowestBidPrice = commodity.getLastBid();
        if (commodity.getLastBidderAccountId() != null) {
            lowestBidPrice += blackMarketAuctionInfo.getPriceRise();
        }
        if (price < lowestBidPrice) {
            throw AuctionException.tooLowBid();
        }

        if (record.getStockYb() >= price) {
            record.decreaseStockYb(price);
        } else {
            currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, price - record.getStockYb(), true, CurrencyConstants.PURPOSE_DECREMENT_拍卖存入元宝);
            record.setStockYb(0);
        }
        record.increaseLockedYb(price);

        if (commodity.getLastBidderAccountId() != null) {
            AuctionRecord lastBidderRecord = auctionRepository.findByIdForWrite(commodity.getLastBidderAccountId()).get();
            lastBidderRecord.decreaseLockedYb(commodity.getLastBid());
            lastBidderRecord.increaseStockYb(commodity.getLastBid());
        }

        commodity.setLastBid(price);
        commodity.setLastBidderAccountId(accountId);

        CommodityPlayerRecord commodityPlayerRecord = commodityPlayerRepository.findOrCreateById(commodityId, accountId);
        commodityPlayerRecord.setBidded(true);

        if (price >= blackMarketAuctionInfo.getBroadcastCondition() && !commodity.isBroadcastPublished()) {
            commodity.setBroadcastPublished(true);
            Player player = playerRepository.findById(accountId).get();
            switch (blackMarketAuctionInfo.getType()) {
                case 3:
                    chatService.sendSystemMessage(
                            ChatConstants.SERVICE_ID_UNDIFINED,
                            ChatMessage.createTemplateMessage(
                                    AuctionConstants.BROADCAST_ID_TITLE,
                                    ImmutableMap.of(
                                            "titleName", resourceContext.getLoader(TitleInformations.class).get(resourceContext.getLoader(GodTitle.class).get(blackMarketAuctionInfo.getAuctionId()).getPrototypeId()).getName(),
                                            "amount", price,
                                            "playerName", player.getPlayerName()
                                    )
                            )
                    );
                    break;
                case 1:
                    Map<String, Object> args = new HashMap<>();
                    TemplateTypes.EquipmentName.addTo(args, "equipment", resourceContext.getLoader(GodEquipment.class).get(blackMarketAuctionInfo.getAuctionId()).getPrototypeId(), player.getPrefabId());
                    args.put("playerName", player.getPlayerName());
                    args.put("amount", price);
                    chatService.sendSystemMessage(
                            ChatConstants.SERVICE_ID_UNDIFINED,
                            ChatMessage.createTemplateMessage(
                                    AuctionConstants.BROADCAST_ID_EQUIPMENT,
                                    args
                            )
                    );
                    break;
                case 2:
                    chatService.sendSystemMessage(
                            ChatConstants.SERVICE_ID_UNDIFINED,
                            ChatMessage.createTemplateMessage(
                                    AuctionConstants.BROADCAST_ID_PET,
                                    ImmutableMap.of(
                                            "petName", resourceContext.getLoader(GodPet.class).get(blackMarketAuctionInfo.getAuctionId()).getPetName(),
                                            "amount", price,
                                            "playerName", player.getPlayerName()
                                    )
                            )
                    );
                    break;
                case 4:
                    chatService.sendSystemMessage(
                            ChatConstants.SERVICE_ID_UNDIFINED,
                            ChatMessage.createTemplateMessage(
                                    AuctionConstants.BROADCAST_ID_CURRENCY,
                                    ImmutableMap.of(
                                            "currency", resourceContext.getLoader(Currency.class).get(blackMarketAuctionInfo.getAuctionId()).getName(),
                                            "amount", price,
                                            "playerName", player.getPlayerName()
                                    )
                            )
                    );
                    break;
            }

            Date deadline = commodity.getDeadline();
            if (deadline.getTime() - currentTime < AuctionConstants.DURATION_BID_NEED_EXTRA_TIME.toMillis()) {
                commodity.setDeadline(new Date(deadline.getTime() + AuctionConstants.DURATION_BID_EXTRA_TIME.toMillis()));
            }
        }

        return commodity;
    }

    public void checkForConclusion() {
        long currentTime = timeProvider.currentTime();
        commodityRepository.findByCommodityStatus(CommodityStatus.ON_SALE).forEach(commodity -> {
            if (commodity.getDeadline().getTime() < currentTime) {
                List<CommodityPlayerRecord> commodityPlayerRecords = commodityPlayerRepository.findByCommodityId(commodity.getId());
                if (commodity.getLastBidderAccountId() != null) {
                    long accountId = commodity.getLastBidderAccountId();
                    AuctionRecord record = auctionRepository.findById(accountId).get();
                    currencyService.increaseCurrency(accountId, CurrencyConstants.ID_元宝, commodity.getLastBid(), CurrencyConstants.PURPOSE_INCREMENT_拍卖取出元宝);
                    currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_元宝, commodity.getLastBid(), true, CurrencyConstants.PURPOSE_DECREMENT_拍卖支付元宝);
                    record.decreaseLockedYb(commodity.getLastBid());
                    new MailSendingRequest()
                            .to(accountId)
                            .template(AuctionConstants.MAIL_ID_CONCLUSION_BIDDER)
                            .commit(mailService);
                    int totalLike = commodityPlayerRecords.stream()
                            .mapToInt(CommodityPlayerRecord::getLikeCount)
                            .sum();
                    if (totalLike != 0) {
                        double awardPerLike = (double) commodity.getLastBid() * 0.1 / totalLike;
                        commodityPlayerRecords.forEach(r -> {
                            if (r.getLikeCount() > 0) {
                                List<CurrencyStack> currencyStacks = new ArrayList<>();
                                long probablyYb = (long) (r.getLikeCount() * awardPerLike);
                                if (probablyYb < 1) {
                                    currencyStacks.add(new CurrencyStack(CurrencyConstants.ID_门贡, Math.max(probablyYb * 50, 10)));
                                } else {
                                    currencyStacks.add(new CurrencyStack(CurrencyConstants.ID_元宝, probablyYb));
                                }
                                new MailSendingRequest()
                                        .to(r.getAccountId())
                                        .template(AuctionConstants.MAIL_ID_CONCLUSION_LIKE_AWARD)
                                        .attachment(currencyStacks)
                                        .attachmentSource(CurrencyConstants.PURPOSE_INCREMENT_拍卖点赞分红)
                                        .commit(mailService);
                            }
                        });
                    }
                    commodity.setCommodityStatus(CommodityStatus.SOLD);
                } else {
                    List<Long> likedAccountIds = commodityPlayerRecords.stream()
                            .filter(it -> it.getLikeCount() > 0)
                            .map(it -> it.getAccountId())
                            .collect(Collectors.toList());
                    new MailSendingRequest()
                            .to(likedAccountIds)
                            .template(AuctionConstants.MAIL_ID_CONCLUSION_NO_LIKE_AWARD)
                            .commit(mailService);
                    int queueNumber = commodity.getQueueNumber() + 10;
                    while (commodityRepository.existsByQueueNumber(queueNumber)) {
                        queueNumber++;
                    }
                    commodity.setQueueNumber(queueNumber);
                    commodity.setCommodityStatus(CommodityStatus.QUEUING);
                }
                commodityPlayerRepository.deleteAll(commodityPlayerRecords);
            }
        });
    }

    public void like(long accountId, long commodityId) {
        AuctionRecord record = auctionRepository.findByIdForWrite(accountId).get();
        if (record.getLikedToday() >= record.getLikedTodayLimit()) {
            throw AuctionException.dailyLikeReachLimit();
        }
        Commodity commodity = commodityRepository.findById(commodityId).get();
        if (!commodity.getCommodityStatus().equals(CommodityStatus.ON_SALE)) {
            throw AuctionException.commodityNotOnSale();
        }
        CommodityPlayerRecord commodityPlayerRecord = commodityPlayerRepository.findOrCreateById(commodityId, accountId);
        commodityPlayerRecord.increaseLikeCount();
        record.increaseLikedToday();
    }

    public CommodityWithdrawResult withdrawAll(long accountId) {
        AuctionRecord record = auctionRepository.findByIdForWrite(accountId).get();
        currencyService.increaseCurrency(accountId, CurrencyConstants.ID_元宝, record.getStockYb(), CurrencyConstants.PURPOSE_INCREMENT_拍卖取出元宝);
        record.setStockYb(0);
        List<Equipment> equipments = new ArrayList<>();
        List<Pet> pets = new ArrayList<>();
        List<Title> titles = new ArrayList<>();
        commodityRepository.findDeliverable(accountId).forEach(commodity -> {
            BlackMarketAuctionInfo blackMarketAuctionInfo = resourceContext.getLoader(BlackMarketAuctionInfo.class).get(commodity.getDefinitionId());
            switch (blackMarketAuctionInfo.getType()) {
                case 1:
                    Equipment equipment = equipmentService.createAndSaveEquipmentByPrototype(accountId, blackMarketAuctionInfo.getAuctionId());
                    equipments.add(equipment);
                    break;
                case 2:
                    Pet pet = petService.createAndSavePetByPrototype(accountId, blackMarketAuctionInfo.getAuctionId());
                    pets.add(pet);
                    break;
                case 3:
                    Title title = titleService.grantTitleByPrototype(accountId, blackMarketAuctionInfo.getAuctionId());
                    titles.add(title);
                    break;
                case 4:
                    currencyService.increaseCurrency(accountId, blackMarketAuctionInfo.getAuctionId(), 1);
                    break;
            }
            commodity.setDelivered(true);
        });
        return new CommodityWithdrawResult(equipments, pets, titles);
    }

    @Override
    public void dailyReset() {
        auctionRepository.findAllForWrite().forEach(record -> {
            record.setLikedToday(0);
            record.setLikedTodayLimit(AuctionConstants.dailyLikeUpperLimit(playerRepository, record.getAccountId()));
        });
    }

}
