/*
 * Created 2017-6-23 12:51:11
 */
package cn.com.yting.kxy.web.gift;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceLoader;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.gift.resource.GiftAndExchangeCodeInformation;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class GiftService {

    private static final Logger LOG = LoggerFactory.getLogger(GiftService.class);

    @Autowired
    private GiftRepository giftRepository;
    @Autowired
    private GiftGeneratingRepository giftGeneratingRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private TimeProvider timeProvider;

    public void redeem(long accountId, String code) {
        Player player = playerRepository.findById(accountId).get();
        if (player.getPlayerLevel() < 30) {
            throw KxyWebException.unknown("角色等级不足");
        }
        Gift gift = giftRepository.findByCode(code);
        if (gift == null) {
            throw GiftException.giftNotFound();
        }
        if (gift.isRedeemed()) {
            throw GiftException.giftRedeemed();
        }

        ResourceLoader<GiftAndExchangeCodeInformation> loader = resourceContext.getLoader(GiftAndExchangeCodeInformation.class);
        if (!loader.exists(gift.getGiftDefinitionId())) {
            throw GiftException.giftNotAvailable();
        }
        GiftAndExchangeCodeInformation definition = loader.get(gift.getGiftDefinitionId());
        if (!definition.getValidPeriod().isValid(timeProvider.currentInstant())) {
            throw GiftException.giftNotAvailable();
        }
        if (giftRepository.countByRedeemer(definition.getId(), accountId) >= definition.getConvertibility()) {
            throw GiftException.overLimitation();
        }

        List<CurrencyStack> currencyStacks = definition.getCurries().stream()
            .map(it -> new CurrencyStack(it.getId(), it.getAmount()))
            .collect(Collectors.toList());
        MailSendingRequest.create()
            .to(accountId)
            .template(definition.getMail())
            .attachment(currencyStacks)
            .attachmentSource(CurrencyConstants.PURPOSE_INCREMENT_礼包兑换)
            .commit(mailService);

        gift.setRedeemed(true);
        gift.setRedeemerAccountId(accountId);
        gift.setRedeemTime(new Date(timeProvider.currentTime()));
    }

    public GiftGeneratingRecord initGiftCodeGeneration(long giftDefinitionId, String prototypeCode, int serialCodeBegin) {
        if (giftGeneratingRepository.existsById(giftDefinitionId)) {
            throw new IllegalStateException("礼包生成已初始化过，id=" + giftDefinitionId);
        }
        GiftGeneratingRecord giftGenerateRecord = new GiftGeneratingRecord();
        giftGenerateRecord.setId(giftDefinitionId);
        giftGenerateRecord.setPrototypeCode(prototypeCode);
        giftGenerateRecord.setSerialCodeBegin(serialCodeBegin);
        return giftGeneratingRepository.save(giftGenerateRecord);
    }

    public List<Gift> generateGiftCodes(long giftDefinitionId, int count) {
        GiftGeneratingRecord giftGenerateRecord = giftGeneratingRepository.findById(giftDefinitionId)
            .orElseThrow(() -> new IllegalStateException("礼包生成未初始化, id=" + giftDefinitionId));
        GiftCodeGenerator codeGenerator = giftGenerateRecord.createGenerator();
        List<Gift> gifts = new ArrayList<>(count);
        Date currentTime = new Date(timeProvider.currentTime());
        for (int i = 0; i < count; i++) {
            Gift gift = new Gift();
            gift.setGiftDefinitionId(giftDefinitionId);
            gift.setCode(codeGenerator.generate().toString());
            gift.setCreateTime(currentTime);
            gifts.add(gift);
        }
        giftGenerateRecord.setGeneratedCount(codeGenerator.getCount());
        return giftRepository.saveAll(gifts);
    }
}
