/*
 * Created 2018-7-27 12:32:58
 */
package cn.com.yting.kxy.web.gift;

import java.util.stream.Collectors;

import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.ModuleApiProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
@RequestMapping("/management/gift")
public class GiftManagementController implements ModuleApiProvider {

    @Autowired
    private GiftGeneratingRepository giftGeneratingRepository;
    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private GiftService giftService;

    @PostMapping("/action/{giftDefinitionId}/init")
    public GiftGeneratingRecord initGiftGenerating(
        @PathVariable("giftDefinitionId") long giftDefinitionId,
        @RequestParam("prototypeCode") String prototypeCode,
        @RequestParam("serialCodeBegin") int serialCodeBegin
    ) {
        return giftService.initGiftCodeGeneration(giftDefinitionId, prototypeCode, serialCodeBegin);
    }

    @PostMapping(path = "/action/{giftDefinitionId}/generate", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generate(
        @PathVariable("giftDefinitionId") long giftDefinitionId,
        @RequestParam("count") int count
    ) {
        return giftService.generateGiftCodes(giftDefinitionId, count).stream()
            .map(Gift::getCode)
            .collect(Collectors.joining("\r\n"));
    }

    @RequestMapping("/view/{giftDefinitionId}")
    public GiftGeneratingRecord view(
        @PathVariable("giftDefinitionId") long giftDefinitionId
    ) {
        return giftGeneratingRepository.findById(giftDefinitionId)
            .orElseThrow(() -> KxyWebException.notFound("礼包生成记录不存在，id=" + giftDefinitionId));
    }

    @RequestMapping(path = "/view/{giftDefinitionId}/codes", produces = MediaType.TEXT_PLAIN_VALUE)
    public String viewCodes(
        @PathVariable("giftDefinitionId") long giftDefinitionId
    ) {
        return giftRepository.findCodeByDefinitionId(giftDefinitionId).stream()
            .collect(Collectors.joining("\r\n"));
    }

    @Override
    public void buildModuleApi(ModuleBuilder<?> builder) {
        builder
            .name("礼包管理")
            .baseUri("/management/gift")
            //
            .webInterface()
            .name("init")
            .uri("/action/{giftDefinitionId}/init")
            .post()
            .description("初始化礼包生成")
            .requestParameter("integer", "giftDefinitionId", "礼包定义的id")
            .requestParameter("string", "prototypeCode", "礼包原型码")
            .requestParameter("integer", "serialCodeBegin", "礼包序列码的开始")
            .response(GiftGeneratingRecord.class, "礼包生成记录")
            .and()
            //
            .webInterface()
            .name("generate")
            .uri("/action/{giftDefinitionId}/generate")
            .post()
            .description("生成礼包")
            .requestParameter("integer", "giftDefinitionId", "礼包定义的id")
            .requestParameter("integer", "count", "礼包序列码的开始")
            .response("string", "文本形式的兑换码列表")
            .and()
            //
            .webInterface()
            .name("view")
            .uri("/view/{giftDefinitionId}")
            .description("查看礼包生成记录")
            .requestParameter("integer", "giftDefinitionId", "礼包定义的id")
            .response(GiftGeneratingRecord.class, "礼包生成记录")
            .and()
            //
            .webInterface()
            .name("viewCodes")
            .uri("/view/{giftDefinitionId}/codes")
            .description("查看礼包兑换码")
            .requestParameter("integer", "giftDefinitionId", "礼包定义的id")
            .response("string", "文本形式的兑换码列表")
            .and();
    }
}
