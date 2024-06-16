/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.shop;

import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resetting.ResetType;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.script.ScriptEngineProvider;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.shop.resource.Commodity;
import cn.com.yting.kxy.web.shop.resource.Shop;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Darkholme
 */
@Service
@Transactional
public class ShopService implements InitializingBean, ResetTask {

    @Autowired
    CurrencyService currencyService;

    @Autowired
    ShopCommodityRepository shopCommodityRepository;

    @Autowired
    ResourceContext resourceContext;

    final Map<Long, List<Long>> shopContainer = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        resourceContext.getLoader(Shop.class).getAll().values().forEach((shop) -> {
            List<Long> commodityList;
            if (shopContainer.containsKey(shop.getShopId())) {
                commodityList = shopContainer.get(shop.getShopId());
            } else {
                commodityList = new ArrayList<>();
            }
            if (!commodityList.contains(shop.getCommodityId())) {
                commodityList.add(shop.getCommodityId());
            }
            shopContainer.put(shop.getShopId(), commodityList);
            //
            if (!shopCommodityRepository.existsById(shop.getCommodityId())) {
                Commodity commodity = resourceContext.getLoader(Commodity.class).get(shop.getCommodityId());
                ShopCommodityRecord cr = new ShopCommodityRecord();
                cr.setCommodityId(commodity.getId());
                cr.setRemainCount(commodity.getReplenishUpperLimit());
                cr.setTotalBuy(0);
                cr.setTotalBuyInPeriod(0);
                cr = refreshCurrentPrice(commodity, cr);
                shopCommodityRepository.save(cr);
            }
        });
    }

    public ShopCommodityRecord getCommodity(long commodityId) {
        return shopCommodityRepository.findByCommodityId(commodityId);
    }

    public List<ShopCommodityRecord> getShop(long shopId) {
        List<ShopCommodityRecord> commodityList = new ArrayList<>();
        if (shopContainer.containsKey(shopId)) {
            shopContainer.get(shopId).forEach((commodityId) -> {
                commodityList.add(shopCommodityRepository.findByCommodityId(commodityId));
            });
        }
        return commodityList;
    }

    public List<ShopCommodityRecord> buy(long accountId, long shopId, long commodityId, long amount, long expectedPrice) {
        if (!shopCommodityRepository.existsById(commodityId)) {
            throw ShopException.commodityNotExist();
        }
        //
        Commodity commodity = resourceContext.getLoader(Commodity.class).get(commodityId);
        if ((amount > 1 && commodity.getAllowBatchBuy() != 1) || amount < 1) {
            throw ShopException.notAllowBatchBuy();
        }
        //
        ShopCommodityRecord cr = shopCommodityRepository.findByCommodityIdForWrite(commodityId).orElse(null);
        if (expectedPrice != cr.getCurrentPrice()) {
            throw ShopException.priceNotMatch();
        }
        //
        if (amount > cr.getRemainCount()) {
            throw ShopException.insufficientRemainCount();
        }
        //
        currencyService.decreaseCurrency(accountId, commodity.getPurchaseCurrencyId(), amount * expectedPrice, true, CurrencyConstants.PURPOSE_DECREMENT_兑换商店购买);
        currencyService.increaseCurrency(accountId, commodity.getCurrencyId(), amount, CurrencyConstants.PURPOSE_INCREMENT_兑换商店产出);
        cr.setRemainCount(cr.getRemainCount() - amount);
        cr.setTotalBuy(cr.getTotalBuy() + amount);
        cr.setTotalBuyInPeriod(cr.getTotalBuyInPeriod() + amount);
        shopCommodityRepository.save(refreshCurrentPrice(commodity, cr));
        //
        return getShop(shopId);
    }

    private ShopCommodityRecord refreshCurrentPrice(Commodity commodity, ShopCommodityRecord cr) {
        ScriptEngine scriptEngine = ScriptEngineProvider.getScriptEngine();
        Bindings bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.clear();
        bindings.put("A", cr.getTotalBuy());
        bindings.put("B", cr.getTotalBuyInPeriod());
        try {
            cr.setCurrentPrice(((Number) scriptEngine.eval(commodity.getUnitPrice())).longValue());
        } catch (ScriptException ex) {
            throw new RuntimeException(ex);
        }
        return cr;
    }

    private ShopCommodityRecord refreshTotalBuyInPeriod(ShopCommodityRecord cr) {
        cr.setTotalBuyInPeriod(0);
        return cr;
    }

    private ShopCommodityRecord refreshRemainCount(Commodity commodity, ShopCommodityRecord cr) {
        cr.setRemainCount(Math.min(cr.getRemainCount() + commodity.getReplenishAmount(), commodity.getReplenishUpperLimit()));
        return cr;
    }

    @Override
    public void anyReset(ResetType resetType) {
        resourceContext.getLoader(Commodity.class)
                .getAll()
                .values()
                .stream()
                .filter((commodity) -> (shopCommodityRepository.existsById(commodity.getId())))
                .forEachOrdered((commodity) -> {
                    ShopCommodityRecord cr = null;
                    if (commodity.getResetCondition() == 1 && resetType == ResetType.DAILY) {
                        cr = shopCommodityRepository.findByCommodityIdForWrite(commodity.getId()).orElse(null);
                        cr = refreshTotalBuyInPeriod(cr);
                    }
                    //
                    if ((commodity.getReplenishCondition() == 1 && resetType == ResetType.DAILY)
                            || (commodity.getReplenishCondition() == 2 && resetType == ResetType.HOURLY)) {
                        cr = shopCommodityRepository.findByCommodityIdForWrite(commodity.getId()).orElse(null);
                        cr = refreshRemainCount(commodity, cr);
                    }
                    //
                    if (cr != null) {
                        cr = refreshCurrentPrice(commodity, cr);
                        shopCommodityRepository.save(cr);
                    }
                });
    }

}
