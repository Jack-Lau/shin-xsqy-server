/*
 * Created 2018-11-7 18:43:33
 */
package cn.com.yting.kxy.web.title;

import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.award.AwardResult;
import cn.com.yting.kxy.web.award.AwardService;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.resource.CurrencyToConsumables;
import cn.com.yting.kxy.web.player.ParameterSpaceProvider;
import cn.com.yting.kxy.web.player.PlayerRelation;
import cn.com.yting.kxy.web.player.PlayerRelationRepository;
import cn.com.yting.kxy.web.title.resource.GodTitle;
import cn.com.yting.kxy.web.title.resource.TitleInformations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class TitleService implements ParameterSpaceProvider {

    @Autowired
    private TitleRepository titleRepository;
    @Autowired
    private TitleGrantingStatsRepository titleGrantingStatsRepository;
    @Autowired
    private PlayerRelationRepository playerRelationRepository;

    @Autowired
    private AwardService awardService;
    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ResourceContext resourceContext;

    public TitleRedeemResult redeemTitle(long accountId, long currencyId) {
        Long definitionId = resourceContext.getLoader(CurrencyToConsumables.class).getAll().values().stream()
                .filter(it -> it.getEffectID() == 3)
                .filter(it -> it.getId() == currencyId)
                .map(it -> it.getEffectParameter())
                .findAny().orElse(null);
        if (definitionId == null) {
            throw TitleException.noSuchRecipe();
        }

        currencyService.decreaseCurrency(accountId, currencyId, 1);
        TitleInformations titleInformations = resourceContext.getLoader(TitleInformations.class).get(definitionId);
        if (titleInformations.getType() == 0) {
            Title existedTitle = titleRepository.findByAccountIdAndDefinitionId(accountId, definitionId).orElse(null);
            if (existedTitle != null) {
                AwardResult awardResult = awardService.processAward(accountId, TitleConstants.AWARD_ID_DUMPLICATED);
                return new TitleRedeemResult(null, awardResult);
            }
        }
        return new TitleRedeemResult(grantTitle(accountId, definitionId), null);
    }

    public void designatePrimaryTitle(long accountId, long titleId) {
        Title title = titleRepository.findById(titleId).orElseThrow(() -> KxyWebException.notFound("称号不存在"));
        title.verifyOwner(accountId);
        PlayerRelation playerRelation = playerRelationRepository.findOrCreate(accountId);
        playerRelation.setTitleId(titleId);
    }

    public void untitle(long accountId) {
        PlayerRelation playerRelation = playerRelationRepository.findOrCreate(accountId);
        playerRelation.setTitleId(null);
    }

    public Title grantTitleForTest(long accountId, long definitionId) {
        return grantTitle(accountId, definitionId);
    }

    public Title grantTitleByPrototype(long accountId, long prototypeId) {
        Title title = createTitleByPrototype(prototypeId);
        title.setAccountId(accountId);
        return titleRepository.saveAndFlush(title);
    }

    public Title createTitleByPrototype(long prototypeId) {
        GodTitle godTitle = resourceContext.getLoader(GodTitle.class).get(prototypeId);

        Title title = new Title();
        title.setDefinitionId(godTitle.getPrototypeId());
        title.setNumber(godTitle.getNowNumber());

        return title;
    }

    private Title grantTitle(long accountId, long definitionId) {
        TitleInformations titleInformations = resourceContext.getLoader(TitleInformations.class).get(definitionId);
        Integer number = null;
        if (titleInformations.getLimitedQuantity() > 0) {
            TitleGrantingStats stats = titleGrantingStatsRepository.findOrCreateById(definitionId);
            if (stats.getGrantedCount() >= titleInformations.getLimitedQuantity()) {
                throw TitleException.limitedQuantityReach();
            }
            stats.increaseGrantedCount();
            number = stats.getGrantedCount();
        }
        if (titleInformations.getType() == 0) {
            Title existedTitle = titleRepository.findByAccountIdAndDefinitionId(accountId, definitionId).orElse(null);
            if (existedTitle != null) {
                return existedTitle;
            }
        }
        Title title = new Title();
        title.setAccountId(accountId);
        title.setDefinitionId(definitionId);
        title.setNumber(number);
        return titleRepository.saveAndFlush(title);
    }

    @Override
    public ParameterSpace createParameterSpace(long accountId) {
        return playerRelationRepository.findById(accountId)
                .map(it -> it.getTitleId())
                .flatMap(it -> titleRepository.findById(it))
                .map(it -> resourceContext.getLoader(TitleInformations.class).get(it.getDefinitionId()))
                .map(it -> it.getParameterSpace())
                .orElse(ParameterSpace.EMPTY);
    }

}
