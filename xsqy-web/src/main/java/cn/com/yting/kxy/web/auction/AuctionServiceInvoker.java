/*
 * Created 2018-11-15 15:55:58
 */
package cn.com.yting.kxy.web.auction;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
public class AuctionServiceInvoker implements InitializingBean, DisposableBean {

    @Autowired
    private AuctionService auctionService;

    @Override
    public void afterPropertiesSet() throws Exception {
        auctionService.init();
    }

    @Override
    public void destroy() throws Exception {
        auctionService.destroy();
    }

    @Scheduled(fixedDelay = 60_000)
    public void minutelyUpdate() {
        auctionService.checkForConclusion();
        auctionService.putOnSale();
    }
}
