/*
 * Created 2018-12-22 11:52:23
 */
package cn.com.yting.kxy.web.market;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/market")
@ModuleDoc(moduleName = "交易行")
@Transactional
public class MarketController {

    @Autowired
    private ConsignmentRepository consignmentRepository;
    @Autowired
    private ConsignmentMarkerRepository consignmentMarkerRepository;

    @Autowired
    private MarketService marketService;

    @Autowired
    private TimeProvider timeProvider;

    @RequestMapping("/consignment/mine")
    @WebInterfaceDoc(description = "查看自己相关的货品，包括自己出售的和可领取的", response = "货品记录")
    public MyConsignmentsComplex viewMyConsignments(
        @AuthenticationPrincipal Account account
    ) {
        Date currentTime = new Date(timeProvider.currentTime());
        List<ConsignmentDetail> onSaleConsignments = new ArrayList<>();
        List<ConsignmentDetail> suspendedConsignments = new ArrayList<>();
        List<ConsignmentDetail> goodsObtainableConsignments = new ArrayList<>();
        List<ConsignmentDetail> paymentObtainableConsignments = new ArrayList<>();
        List<ConsignmentDetail> archiveConsignments = new ArrayList<>();
        consignmentRepository.findBySellerAccountId(account.getId())
            .forEach(consignment -> {
                ConsignmentDetail detail = marketService.consignmentToDetail(consignment);
                if (consignment.isOnSale(currentTime)) {
                    onSaleConsignments.add(detail);
                } else if (!consignment.isSold()) {
                    suspendedConsignments.add(detail);
                } else if (!consignment.isPaymentDelivered()) {
                    paymentObtainableConsignments.add(detail);
                } else {
                    archiveConsignments.add(detail);
                }
            });
        consignmentRepository.findByBuyerAccountId(account.getId())
            .forEach(consignment -> {
                ConsignmentDetail detail = marketService.consignmentToDetail(consignment);
                if (!consignment.isGoodsDelivered()) {
                    goodsObtainableConsignments.add(detail);
                } else {
                    archiveConsignments.add(detail);
                }
            });
        return new MyConsignmentsComplex(
            onSaleConsignments,
            suspendedConsignments,
            goodsObtainableConsignments,
            paymentObtainableConsignments,
            archiveConsignments
        );
    }

    @RequestMapping("/consignment/marked")
    @WebInterfaceDoc(description = "查看自己收藏的货品", response = "货品记录")
    public List<ConsignmentDetail> viewMyMarkedConsignments(
        @AuthenticationPrincipal Account account
    ) {
        return consignmentRepository.findByMarker(account.getId()).stream()
            .map(marketService::consignmentToDetail)
            .collect(Collectors.toList());
    }

    @RequestMapping("/consignment/")
    @WebInterfaceDoc(description = "查看所有上架中的货品", response = "货品记录")
    public PagedConsignmentList viewConsignments(
        @AuthenticationPrincipal Account account,
        Pageable pageable
    ) {
        return PagedConsignmentList.from(
            consignmentRepository.findByOnSaleOrdered(new Date(timeProvider.currentTime()), account.getId(), pageable)
            .map(marketService::consignmentToDetail)
        );
    }

    @RequestMapping("/consignment/equipments")
    @WebInterfaceDoc(description = "查看上架中的装备货品", response = "货品记录")
    public PagedConsignmentList viewEquipmentConsignments(
        @AuthenticationPrincipal Account account,
        @RequestParam(name = "part", required = false) @ParamDoc("装备部位筛选条件（可选）") Integer part,
        @RequestParam(name = "color", required = false) @ParamDoc("品质筛选条件（可选）") Integer color,
        @RequestParam(name = "maxEnhanceLevel", required = false) @ParamDoc("最大强化等级匹配条件（可选）") Integer maxEnhanceLevel,
        @RequestParam(name = "paramMatch", defaultValue = "all") @ParamDoc("属性筛选条件匹配方式，'all' 为全条件匹配，'any' 为任意条件匹配，（可选，默认 'all'）") String paramMatch,
        @RequestParam(name = "patk", required = false) @ParamDoc("物伤匹配条件（可选）") Integer patk,
        @RequestParam(name = "matk", required = false) @ParamDoc("法伤匹配条件（可选）") Integer matk,
        @RequestParam(name = "fc", required = false) @ParamDoc("战斗力匹配条件（可选）") Long fc,
        @RequestParam(name = "effectMatch", defaultValue = "all") @ParamDoc("特效筛选条件匹配方式，'all' 为全条件匹配，'any' 为任意条件匹配，（可选，默认 'all'）") String effectMatch,
        @RequestParam(name = "effectIds", required = false) @ParamDoc("装备特效匹配条件，逗号分隔列表（可选）") String effectIdsText,
        @RequestParam(name = "skillEnhancementEffectIds", required = false) @ParamDoc("门派技能强化匹配条件，逗号分隔列表（可选）") String skillEnhancementEffectIds,
        Pageable pageable
    ) {
        EquipmentQueryParameter param = new EquipmentQueryParameter(part, color, maxEnhanceLevel, paramMatch, patk, matk, fc, effectMatch, effectIdsText, skillEnhancementEffectIds);
        Page<Consignment> page = consignmentRepository.findEquipmentConsignments(account.getId(), new Date(timeProvider.currentTime()), param, pageable);
        return PagedConsignmentList.from(page.map(marketService::consignmentToDetail));
    }

    @RequestMapping("/consignment/pets")
    @WebInterfaceDoc(description = "查看上架中的宠物货品", response = "货品记录")
    public PagedConsignmentList viewPetConsignments(
        @AuthenticationPrincipal Account account,
        @RequestParam(name = "petDefinitionId", required = false) @ParamDoc("宠物种类筛选条件（可选）") Long petDefinitionId,
        @RequestParam(name = "petRank", required = false) @ParamDoc("冲星匹配条件（可选）") Integer petRank,
        @RequestParam(name = "maxPetRank", required = false) @ParamDoc("冲星上限匹配条件（可选）") Integer maxPetRank,
        @RequestParam(name = "aptitudeHp", required = false) @ParamDoc("生命资质匹配条件（可选）") Integer aptitudeHp,
        @RequestParam(name = "aptitudeAtk", required = false) @ParamDoc("攻击资质匹配条件（可选）") Integer aptitudeAtk,
        @RequestParam(name = "aptitudePdef", required = false) @ParamDoc("物防资质匹配条件（可选）") Integer aptitudePdef,
        @RequestParam(name = "aptitudeMdef", required = false) @ParamDoc("法防资质匹配条件（可选）") Integer aptitudeMdef,
        @RequestParam(name = "aptitudeSpd", required = false) @ParamDoc("速度资质匹配条件（可选）") Integer aptitudeSpd,
        @RequestParam(name = "abilitiyMatch", defaultValue = "all") @ParamDoc("技能筛选条件匹配方式，'all' 为全条件匹配，'any' 为任意条件匹配，（可选，默认 'all'）") String abilitiyMatch,
        @RequestParam(name = "abilityIds", required = false) @ParamDoc("技能匹配条件，逗号分隔列表（可选）") String abilityIdsText,
        Pageable pageable
    ) {
        PetQueryParameter param = new PetQueryParameter(petDefinitionId, petRank, maxPetRank, aptitudeHp, aptitudeAtk, aptitudePdef, aptitudeMdef, aptitudeSpd, abilitiyMatch, abilityIdsText);
        Page<Consignment> page = consignmentRepository.findPetConsignments(account.getId(), new Date(timeProvider.currentTime()), param, pageable);
        return PagedConsignmentList.from(page.map(marketService::consignmentToDetail));
    }

    @RequestMapping("/consignment/titles")
    @WebInterfaceDoc(description = "查看上架中的称号货品", response = "货品记录")
    public PagedConsignmentList viewTitleConsignments(
        @AuthenticationPrincipal Account account,
        Pageable pageable
    ) {
        Page<Consignment> page = consignmentRepository.findByGoodsTypeOnSaleOrdered(new Date(timeProvider.currentTime()), account.getId(), GoodsType.TITLE, pageable);
        return PagedConsignmentList.from(page.map(marketService::consignmentToDetail));
    }

    @PostMapping("/consignment/create")
    @WebInterfaceDoc(description = "上架货品（从身上上架）", response = "创建的货品记录")
    public Consignment createConsignment(
        @AuthenticationPrincipal Account account,
        @RequestParam("goodsType") @ParamDoc("货品类型") GoodsType goodsType,
        @RequestParam("goodsObjectId") @ParamDoc("货品对象的id") long goodsObjectId,
        @RequestParam("price") @ParamDoc("售出的价格") long price
    ) {
        return marketService.createConsignment(account.getId(), goodsType, goodsObjectId, price);
    }

    @PostMapping("/consignment/{id}/suspend")
    @WebInterfaceDoc(description = "下架货品", response = "货品记录")
    public Consignment suspendConsignment(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("货品id") long consignmentId
    ) {
        return marketService.suspendConsignment(account.getId(), consignmentId);
    }

    @PostMapping("/consignment/{id}/resume")
    @WebInterfaceDoc(description = "上架货品（从临时仓库上架）", response = "货品记录")
    public Consignment resumeConsignment(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("货品id") long consignmentId,
        @RequestParam("price") @ParamDoc("售出的价格") long price
    ) {
        return marketService.resumeConsignment(account.getId(), consignmentId, price);
    }

    @PostMapping("/consignment/{id}/cancel")
    @WebInterfaceDoc(description = "临时仓库取回（普通取回）", response = "")
    public WebMessageWrapper cancelConsignment(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("货品id") long consignmentId
    ) {
        marketService.cancelConsignment(account.getId(), consignmentId);
        return WebMessageWrapper.ok();
    }

    @RequestMapping("/marker/mine")
    @WebInterfaceDoc(description = "查看自己的收藏", response = "收藏记录")
    public List<ConsignmentMarker> viewMarkers(@AuthenticationPrincipal Account account) {
        return consignmentMarkerRepository.findByAccountId(account.getId());
    }

    @PostMapping("/consignment/{id}/mark")
    @WebInterfaceDoc(description = "收藏指定货品", response = "收藏记录")
    public ConsignmentMarker markConsignment(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("货品id") long consignmentId
    ) {
        return marketService.mark(account.getId(), consignmentId);
    }

    @PostMapping("/consignment/{id}/unmark")
    @WebInterfaceDoc(description = "取消收藏指定货品", response = "")
    public WebMessageWrapper unmarkConsignment(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("货品id") long consignmentId
    ) {
        marketService.unmark(account.getId(), consignmentId);
        return WebMessageWrapper.ok();
    }

    @PostMapping("/consignment/{id}/purchase")
    @WebInterfaceDoc(description = "购买货品", response = "货品记录")
    public Consignment purchase(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("货品id") long consignmentId
    ) {
        return marketService.purchase(account.getId(), consignmentId);
    }

    @PostMapping("/consignment/{id}/obtainPayment")
    @WebInterfaceDoc(description = "临时仓库取回（块币取回）", response = "货品记录")
    public Consignment obtainPayment(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("货品id") long consignmentId
    ) {
        return marketService.obtainPayment(account.getId(), consignmentId);
    }

    @PostMapping("/consignment/{id}/obtainGoods")
    @WebInterfaceDoc(description = "临时仓库取回（购买取回）", response = "货品记录")
    public Consignment obtainGoods(
        @AuthenticationPrincipal Account account,
        @PathVariable("id") @ParamDoc("货品id") long consignmentId
    ) {
        return marketService.obtainGoods(account.getId(), consignmentId);
    }
}
