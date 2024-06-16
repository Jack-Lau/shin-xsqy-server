/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.fashion;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.wordfilter.ForbiddenWordsChecker;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.resource.CurrencyToConsumables;
import cn.com.yting.kxy.web.fashion.resource.FashionDyeCost;
import cn.com.yting.kxy.web.fashion.resource.FashionInfo;
import cn.com.yting.kxy.web.player.PlayerRelation;
import cn.com.yting.kxy.web.player.PlayerRelationRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Darkholme
 */
@Service
@Transactional
public class FashionService {

    @Autowired
    FashionRepository fashionRepository;
    @Autowired
    FashionDyeRepository fashionDyeRepository;
    @Autowired
    FashionGrantingStatsRepository fashionGrantingStatsRepository;
    @Autowired
    PlayerRelationRepository playerRelationRepository;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    ResourceContext resourceContext;
    @Autowired
    ForbiddenWordsChecker forbiddenWordsChecker;

    public Fashion getFashion(long fashionId) {
        return fashionRepository.findById(fashionId).orElse(null);
    }

    public FashionDye getDye(long dyeId) {
        return fashionDyeRepository.findById(dyeId).orElse(null);
    }

    public List<Fashion> getByAccountId(long accountId) {
        return fashionRepository.findByAccountId(accountId);
    }

    public List<FashionDye> getDyeByAccountIdAndDefinitionId(long accountId, long definitionId) {
        return fashionDyeRepository.findByAccountIdAndDefinitionId(accountId, definitionId);
    }

    public Fashion redeem(long accountId, long currencyId) {
        Long definitionId = resourceContext.getLoader(CurrencyToConsumables.class).getAll().values().stream()
                .filter(it -> it.getEffectID() == 6)
                .filter(it -> it.getId() == currencyId)
                .map(it -> it.getEffectParameter())
                .findAny().orElse(null);
        if (definitionId == null) {
            throw FashionException.noSuchRecipe();
        }
        //
        currencyService.decreaseCurrency(accountId, currencyId, 1);
        return grant(accountId, definitionId);
    }

    public void putOn(long accountId, long fashionId) {
        Fashion fashion = fashionRepository.findById(fashionId).orElseThrow(() -> KxyWebException.notFound("时装不存在"));
        fashion.verifyOwner(accountId);
        PlayerRelation playerRelation = playerRelationRepository.findOrCreate(accountId);
        playerRelation.setFashionId(fashionId);
    }

    public void putOff(long accountId) {
        PlayerRelation playerRelation = playerRelationRepository.findOrCreate(accountId);
        playerRelation.setFashionId(null);
    }

    public Fashion addDye(long accountId, long fashionId, FashionDye fashionDye) {
        Fashion fashion = fashionRepository.findByIdForWrite(fashionId).orElse(null);
        if (fashion != null) {
            if (forbiddenWordsChecker.check(fashionDye.getDyeName())) {
                throw FashionException.dyeNameIllegal();
            }
            FashionInfo fi = resourceContext.getLoader(FashionInfo.class).get(fashionDye.getDefinitionId());
            FashionDyeCost fdc = resourceContext.getLoader(FashionDyeCost.class).get(fi.getDyeCostModel());
            long totalCost = 0;
            //
            totalCost += fashionDye.getPart_1_color() != FashionConstants.DEFAULT_COLOR_VALUE ? fdc.getDyeCost(1).getColorCost() : 0;
            totalCost += fashionDye.getPart_1_saturation() != FashionConstants.DEFAULT_SATURATION_VALUE ? fdc.getDyeCost(1).getSaturationCost() : 0;
            totalCost += fashionDye.getPart_1_brightness() != FashionConstants.DEFAULT_BRIGHTNESS_VALUE ? fdc.getDyeCost(1).getBrightnessCost() : 0;
            //
            totalCost += fashionDye.getPart_2_color() != FashionConstants.DEFAULT_COLOR_VALUE ? fdc.getDyeCost(2).getColorCost() : 0;
            totalCost += fashionDye.getPart_2_saturation() != FashionConstants.DEFAULT_SATURATION_VALUE ? fdc.getDyeCost(2).getSaturationCost() : 0;
            totalCost += fashionDye.getPart_2_brightness() != FashionConstants.DEFAULT_BRIGHTNESS_VALUE ? fdc.getDyeCost(2).getBrightnessCost() : 0;
            //
            totalCost += fashionDye.getPart_3_color() != FashionConstants.DEFAULT_COLOR_VALUE ? fdc.getDyeCost(3).getColorCost() : 0;
            totalCost += fashionDye.getPart_3_saturation() != FashionConstants.DEFAULT_SATURATION_VALUE ? fdc.getDyeCost(3).getSaturationCost() : 0;
            totalCost += fashionDye.getPart_3_brightness() != FashionConstants.DEFAULT_BRIGHTNESS_VALUE ? fdc.getDyeCost(3).getBrightnessCost() : 0;
            //
            if (totalCost != 0) {
                currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_染色剂, totalCost, true);
                FashionDye fd = new FashionDye();
                fd.setAccountId(accountId);
                fd.setDefinitionId(fashion.getDefinitionId());
                fd.setDyeName(fashionDye.getDyeName());
                fd.setPart_1_color(fashionDye.getPart_1_color());
                fd.setPart_1_saturation(fashionDye.getPart_1_saturation());
                fd.setPart_1_brightness(fashionDye.getPart_1_brightness());
                //
                fd.setPart_2_color(fashionDye.getPart_2_color());
                fd.setPart_2_saturation(fashionDye.getPart_2_saturation());
                fd.setPart_2_brightness(fashionDye.getPart_2_brightness());
                //
                fd.setPart_3_color(fashionDye.getPart_3_color());
                fd.setPart_3_saturation(fashionDye.getPart_3_saturation());
                fd.setPart_3_brightness(fashionDye.getPart_3_brightness());
                //
                fd = fashionDyeRepository.saveAndFlush(fd);
                //
                fashion.setDyeId(fd.getId());
                return fashionRepository.save(fashion);
            } else {
                return fashion;
            }
        } else {
            return null;
        }
    }

    public Fashion chooseDye(long accountId, long fashionId, long dyeId) {
        Fashion fashion = fashionRepository.findById(fashionId).orElseThrow(() -> KxyWebException.notFound("时装不存在"));
        fashion.verifyOwner(accountId);
        //
        FashionDye fd = fashionDyeRepository.findById(dyeId).orElse(null);
        if (fd != null) {
            fd.verifyOwner(accountId);
            if (fd.getDefinitionId() == fashion.getDefinitionId()
                    && fd.getId() != fashion.getDyeId()) {
                FashionInfo fi = resourceContext.getLoader(FashionInfo.class).get(fashion.getDefinitionId());
                currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_染色剂, fi.getChangeDyeCost(), true);
                fashion.setDyeId(fd.getId());
                fashion = fashionRepository.save(fashion);
            }
        }
        return fashion;
    }

    public Fashion putOffDye(long accountId, long fashionId) {
        Fashion fashion = fashionRepository.findById(fashionId).orElseThrow(() -> KxyWebException.notFound("时装不存在"));
        fashion.verifyOwner(accountId);
        //
        fashion.setDyeId(0);
        fashion = fashionRepository.save(fashion);
        return fashion;
    }

    private Fashion grant(long accountId, long definitionId) {
        FashionInfo fi = resourceContext.getLoader(FashionInfo.class).get(definitionId);
        Integer number = null;
        if (fi.getLimitedQuantity() > 0) {
            FashionGrantingStats fgs = fashionGrantingStatsRepository.findOrCreateById(definitionId);
            if (fgs.getGrantedCount() >= fi.getLimitedQuantity()) {
                throw FashionException.limitedQuantityReach();
            }
            fgs.increaseGrantedCount();
            fashionGrantingStatsRepository.save(fgs);
            number = fgs.getGrantedCount();
        }
        //
        Fashion fashion = new Fashion();
        fashion.setAccountId(accountId);
        fashion.setDefinitionId(definitionId);
        fashion.setDyeId(0);
        fashion.setNumber(number);
        return fashionRepository.saveAndFlush(fashion);
    }

}
