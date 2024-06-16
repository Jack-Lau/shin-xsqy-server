/*
 * Created 2018-10-11 10:48:38
 */
package cn.com.yting.kxy.web.pet;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.DebugOnly;
import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.util.CommaSeparatedLists;
import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.price.PriceConstants;
import cn.com.yting.kxy.web.price.PriceService;
import cn.com.yting.kxy.web.recycling.RecyclingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/pet")
public class PetController implements ModuleApiProvider {

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private PetGachaRankingRepository petGachaRankingRepository;
    @Autowired
    private PetGachaRankingAwardRepository petGachaRankingAwardRepository;
    @Autowired
    private PetGachaRankingSharedRepository petGachaRankingSharedRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PetService petService;
    @Autowired
    private PriceService priceService;
    @Autowired
    private PetGachaRankingService petGachaRankingService;

    @Autowired
    private ResourceContext resourceContext;

    @RequestMapping("/view/mine/id")
    public List<Long> viewMineId(@AuthenticationPrincipal Account account) {
        return petRepository.findIdsByAccountId(account.getId());
    }

    @RequestMapping("/view/legendaryOfMine/id")
    public List<Long> viewLegendaryOfMineId(@AuthenticationPrincipal Account account) {
        return petRepository.findLegendaryIdsByAccountId(account.getId());
    }

    @RequestMapping("/view/{id}/parameters")
    public List<Parameter> viewParameters(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long petId
    ) {
        return petService.getPetParameters(petId);
    }

    @RequestMapping("/viewMineHasCandidateAbilitiesIds")
    public List<Long> viewMineHasCandidateAbilitiesIds(@AuthenticationPrincipal Account account) {
        return petRepository.findByAccountIdAndCandidateAbilitiesTextNotEmpty(account.getId());
    }

    @PostMapping("/action/{id}/rename")
    public Pet rename(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long petId,
            @RequestParam("newName") String newName
    ) {
        return petService.rename(account.getId(), petId, newName);
    }

    @PostMapping("/action/{id}/gachaAbility")
    public PetGachaAbilityResult gachaAbility(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long petId
    ) {
        return petService.gachaAbility(account.getId(), petId);
    }

    // typo
    @PostMapping("/action/{id}/aquireAbility")
    public Pet aquireAbility(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long petId,
            @RequestParam("abilityid") long abilityId
    ) {
        return petService.acquireAbility(account.getId(), petId, abilityId);
    }

    @PostMapping("/action/{id}/enhance")
    public PetEnhanceResult enhance(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long petId
    ) {
        return petService.enhance(account.getId(), petId);
    }

    @PostMapping("/action/{id}/fusion")
    public PetFusionResult fusion(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long petId,
            @RequestParam("subPetId") long subPetId
    ) {
        return petService.fusion(account.getId(), petId, subPetId);
    }

    @PostMapping("/action/{id}/soul")
    public Pet soul(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long petId
    ) {
        return petService.soul(account.getId(), petId);
    }

    @PostMapping("/action/{id}/wash")
    public Pet wash(
            @AuthenticationPrincipal Account account,
            @PathVariable("id") long petId
    ) {
        return petService.wash(account.getId(), petId);
    }

    @RequestMapping("/viewDetail")
    public List<PetDetail> viewDetail(@RequestParam("petIds") String petIdsText) {
        return CommaSeparatedLists.fromText(petIdsText, Long::valueOf).stream()
                .map(it -> petRepository.findById(it)
                .map(pet -> pet.toDetail())
                .orElse(null))
                .collect(Collectors.toList());
    }

    @PostMapping("/modifyBattleList")
    public WebMessageWrapper modifyBattleList(
            @AuthenticationPrincipal Account account,
            @RequestParam("petIds") String petIdsText
    ) {
        petService.modifyBattleList(account.getId(), CommaSeparatedLists.fromText(petIdsText, Long::valueOf));
        return WebMessageWrapper.ok();
    }

    @DebugOnly
    @PostMapping("/createForTest")
    public Pet createForTest(
            @AuthenticationPrincipal Account account,
            @RequestParam("definitionId") long definitionId
    ) {
        return petService.createForTest(account.getId(), definitionId);
    }

    @RequestMapping("/gachaPrice")
    public long gachaPrice() {
        return priceService.getCurrentPrice(PriceConstants.ID_宠物获得);
    }

    @PostMapping("/gacha")
    public PetDetail gacha(
            @AuthenticationPrincipal Account account,
            @RequestParam("expectedPrice") long expectedPrice
    ) {
        return petService.gacha(account.getId(), expectedPrice);
    }

    @RequestMapping("/latestInterestingGachas")
    public Collection<PetGachaLog> latestInterestingGachas() {
        return petService.getLatestInterestingGachas();
    }

    @DebugOnly
    @PostMapping("/triggerDailyReset")
    public void triggerDailyReset() {
        petGachaRankingService.dailyReset();
    }

    @RequestMapping("/gachaRanking")
    public List<PetGachaRankingRecord> gachaRanking() {
        return petGachaRankingRepository.findOrderByRanking(PageRequest.of(0, 12));
    }

    @RequestMapping("/gachaRankingAward")
    public List<PetGachaRankingAwardRecord> gachaRankingAward() {
        return petGachaRankingAwardRepository.findAll();
    }

    @PostMapping("/obtainRankingAward")
    public PetGachaRankingAwardResult obtainRankingAward(@AuthenticationPrincipal Account account) {
        return petGachaRankingService.obtainAward(account.getId());
    }

    @RequestMapping("/gachaRankingShared")
    public PetGachaRankingSharedRecord gachaRankingShared() {
        return petGachaRankingSharedRepository.getTheRecord();
    }

    @PostMapping("/recycle")
    public List<RecyclingResult> recycle(
            @AuthenticationPrincipal Account account,
            @RequestParam("petIds") String petIds
    ) {
        return petService.recycle(account.getId(), CommaSeparatedLists.fromText(petIds, Long::parseLong));
    }

    @RequestMapping("/prototype/{id}")
    public PetDetail viewPrototype(@PathVariable("id") long prototypeId) {
        return petService.createPetByPrototype(prototypeId).toExtraDetail(playerRepository, resourceContext);
    }

    @PostMapping("/redeem")
    public PetDetail redeem(
            @AuthenticationPrincipal Account account,
            @RequestParam("currencyId") long currencyId
    ) {
        return petService.redeem(account.getId(), currencyId);
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
                .name("pet")
                .baseUri("/pet")
                //
                .webInterface()
                .name("viewMineId")
                .uri("/view/mine/id")
                .description("获得所有的自己宠物的 id 的列表")
                .responseArray("number", "宠物的 id")
                .and()
                //
                .webInterface()
                .name("viewLegendaryOfMineId")
                .uri("/view/legendaryOfMine/id")
                .description("获得所有的自己神兽的 id 的列表")
                .responseArray("number", "宠物的 id")
                .and()
                //
                .webInterface()
                .name("viewParameters")
                .uri("/view/{id}/parameters")
                .description("查询指定宠物的能力参数")
                .requestParameter("number", "id", "宠物的 id")
                .responseArray(Parameter.class, "宠物的能力参数集合")
                .and()
                //
                .webInterface()
                .uri("/viewMineHasCandidateAbilitiesIds")
                .description("查询自己的拥有候选技能的宠物的 id 的列表")
                .responseArray("number", "宠物的 id")
                .and()
                //
                .webInterface()
                .name("rename")
                .uri("/action/{id}/rename")
                .post()
                .description("宠物改名")
                .requestParameter("number", "id", "宠物的 id")
                .requestParameter("string", "newName", "新名字")
                .response(Pet.class, "修改后的宠物信息")
                .and()
                //
                .webInterface()
                .name("gachaAbility")
                .uri("/action/{id}/gachaAbility")
                .post()
                .description("抽选宠物技能")
                .requestParameter("number", "id", "宠物的 id")
                .response(PetGachaAbilityResult.class, "抽选的结果")
                .and()
                //
                .webInterface()
                .name("aquireAbility")
                .uri("/action/{id}/aquireAbility")
                .post()
                .description("学习宠物技能")
                .requestParameter("number", "id", "宠物的 id")
                .requestParameter("number", "abilityid", "技能的 id")
                .response(Pet.class, "修改后的宠物信息")
                .and()
                //
                .webInterface()
                .name("enhance")
                .uri("/action/{id}/enhance")
                .post()
                .description("宠物冲星")
                .requestParameter("number", "id", "宠物的 id")
                .response(PetEnhanceResult.class, "结果")
                .and()
                //
                .webInterface()
                .name("fusion")
                .uri("/action/{id}/fusion")
                .post()
                .description("宠物炼化")
                .requestParameter("number", "id", "宠物的 id")
                .requestParameter("number", "subPetId", "作为材料的宠物的 id")
                .response(PetFusionResult.class, "炼化结果")
                .and()
                //
                .webInterface()
                .name("soul")
                .uri("/action/{id}/soul")
                .post()
                .description("宠物附魂-提升附魂等级")
                .requestParameter("number", "id", "需要提升附魂等级的宠物id")
                .response(Pet.class, "附魂结束的宠物")
                .and()
                //
                .webInterface()
                .name("wash")
                .uri("/action/{id}/wash")
                .post()
                .description("宠物附魂-洗炼附魂词条")
                .requestParameter("number", "id", "需要洗炼附魂词条的宠物id")
                .response(Pet.class, "附魂结束的宠物")
                .and()
                //
                .webInterface()
                .uri("/viewDetail")
                .description("查询指定的宠物的详细信息")
                .requestParameter("string", "petIds", "逗号分隔的宠物 id 的列表")
                .responseArray(PetDetail.class, "宠物的详细信息")
                .and()
                //
                .webInterface()
                .uri("/modifyBattleList")
                .post()
                .description("设置宠物出战队列")
                .requestParameter("string", "petIds", "逗号分隔的宠物 id 的列表")
                .and()
                //
                .webInterface()
                .uri("/createForTest")
                .post()
                .description("生成一个宠物（测试用）")
                .requestParameter("number", "definitionId", "配置表的定义 id")
                .response(Pet.class, "生成的宠物")
                .and()
                //
                .webInterface()
                .uri("/gachaPrice")
                .description("查询当前获得宠物的价格")
                .response("number", "价格")
                .and()
                //
                .webInterface()
                .uri("/gacha")
                .post()
                .description("获得宠物")
                .requestParameter("number", "expectedPrice", "期望的价格")
                .response(PetDetail.class, "生成的宠物的详细信息")
                .and()
                //
                .webInterface()
                .uri("/latestInterestingGachas")
                .description("查询最近的广播信息")
                .responseArray(PetGachaLog.class, "获得宠物记录")
                .and()
                //
                .webInterface()
                .uri("/triggerDailyReset")
                .post()
                .description("触发每日重置（测试用）")
                .and()
                //
                .webInterface()
                .uri("/gachaRanking")
                .description("查询宠物获得排行榜")
                .responseArray(PetGachaRankingRecord.class, "排行记录")
                .and()
                //
                .webInterface()
                .uri("/gachaRankingAward")
                .description("查询宠物获得排行榜奖励信息")
                .responseArray(PetGachaRankingAwardRecord.class, "排行奖励记录")
                .and()
                //
                .webInterface()
                .uri("/gachaRankingShared")
                .description("查询宠物获得排行榜的共享信息")
                .response(PetGachaRankingSharedRecord.class, "排行共享记录")
                .and()
                //
                .webInterface()
                .uri("/obtainRankingAward")
                .post()
                .description("领取排行奖励")
                .response(PetGachaRankingAwardResult.class, "奖励结果")
                .and()
                //
                .webInterface()
                .uri("/recycle")
                .post()
                .description("宠物回收")
                .requestParameter("string", "petIds", "逗号分隔的宠物 id 列表")
                .responseArray(RecyclingResult.class, "回收结果")
                .and()
                //
                .webInterface()
                .uri("/prototype/{id}")
                .description("取得从一个原型能够生成的宠物的视图")
                .requestParameter("number", "id", "神宠的定义 id")
                .response(PetDetail.class, "对应的宠物")
                .and()
                //
                .webInterface()
                .uri("/redeem")
                .post()
                .description("兑换一只宠物")
                .requestParameter("number", "currencyId", "用来兑换的货币 id")
                .response(PetDetail.class, "兑换结果")
                .and();
    }

}
