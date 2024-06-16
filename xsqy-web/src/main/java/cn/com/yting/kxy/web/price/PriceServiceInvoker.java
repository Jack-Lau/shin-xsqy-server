/*
 * Created 2018-9-20 10:45:17
 */
package cn.com.yting.kxy.web.price;

import java.util.Date;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.price.resource.FloatingPrice;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
public class PriceServiceInvoker implements InitializingBean {

    @Autowired
    private PriceService priceSerivce;

    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private TimeProvider timeProvider;

    @Override
    public void afterPropertiesSet() throws Exception {
        resourceContext.getLoader(FloatingPrice.class).getAll().values().forEach(floatingPrice -> {
            priceSerivce.initRecord(floatingPrice);
            final long interval = floatingPrice.getReduceCondition() * 1000;
            long startTime = timeProvider.currentTime() + interval;
            taskScheduler.scheduleAtFixedRate(() -> priceSerivce.reducePrice(floatingPrice), new Date(startTime), interval);
        });
    }

}
