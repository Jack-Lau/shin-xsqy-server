/*
 * Created 2019-2-20 18:54:14
 */
package cn.com.yting.kxy.web.topone;

import cn.com.yting.kxy.web.message.RawWebMessageWrapper;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/public/topOne")
public class TopOnePublicController {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/orderNotify")
    public Object orderNotify(OrderNotifyRequest request) {
        eventPublisher.publishEvent(new OrderNotificationEvent(this, request));
        return new RawWebMessageWrapper(ImmutableMap.of("code", 0, "msg", "OK"));
    }
}
