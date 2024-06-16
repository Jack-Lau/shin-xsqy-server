/*
 * Created 2018-9-3 17:54:15
 */
package cn.com.yting.kxy.web.game.yibenwanli;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 在 YibenwanliService 上调用初始化、销毁、计划任务等方法的调用器。 方便直接使用 YibenwanliService 上标注的事务管理
 *
 * @author Azige
 */
@Component
public class YibenwanliServiceInvoker implements InitializingBean, DisposableBean {

    @Autowired
    private YibenwanliService yibenwanliService;

    @Override
    public void afterPropertiesSet() throws Exception {
        yibenwanliService.init();
    }

    @Override
    public void destroy() throws Exception {
        yibenwanliService.destroy();
    }

    @Scheduled(cron = "0 * * * * *")
    public void minutelyUpdate() {
        yibenwanliService.checkForStart();
        yibenwanliService.checkForConclusion();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void hourlyUpdate() {
        yibenwanliService.checkForStart();
        yibenwanliService.checkForConclusion();
    }

}
