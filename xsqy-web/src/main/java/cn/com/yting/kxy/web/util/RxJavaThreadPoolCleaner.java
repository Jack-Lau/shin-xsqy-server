/*
 * Created 2018-11-12 12:38:36
 */
package cn.com.yting.kxy.web.util;

import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
public class RxJavaThreadPoolCleaner implements DisposableBean {

    @Override
    public void destroy() throws Exception {
        Schedulers.shutdown();
    }

}
