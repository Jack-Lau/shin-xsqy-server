/*
 * Created 2018-11-2 16:19:11
 */
package cn.com.yting.kxy.web.awardpool;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
public class AwardPoolServiceInvoker implements InitializingBean {

    @Autowired
    private AwardPoolService awardPoolService;

    @Override
    public void afterPropertiesSet() throws Exception {
        awardPoolService.initPools();
    }
}
