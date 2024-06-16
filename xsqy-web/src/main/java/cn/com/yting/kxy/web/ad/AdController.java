// Created 2021/8/25 16:16
package cn.com.yting.kxy.web.ad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Azige
 */
@RestController
@RequestMapping("/public/ad")
public class AdController {

    private static final Logger LOG = LoggerFactory.getLogger(AdController.class);

    @Autowired
    private AdService adService;

    @GetMapping(path = "/onReward", produces = MediaType.APPLICATION_JSON_VALUE)
    public String onReward(
            @RequestParam("pid") String pid,
            @RequestParam("appid") String appid,
            @RequestParam("transid") String transid,
            @RequestParam("userid") long userid,
            @RequestParam("extrainfo") String extrainfo,
            @RequestParam("sig") String sig
    ) {
        /*
        try {
            if (adService.verifyAndPublishEvent(pid, userid, transid, extrainfo, sig)) {
                return "{\"isValid\":true}";
            }
        } catch (Exception ex) {
            LOG.warn("广告回调验证失败", ex);
        }
         */
        return "{\"isValid\":false}";
    }
}
