/*
 * Created 2018-7-4 11:41:04
 */
package cn.com.yting.kxy.web.util;

import cn.com.yting.kxy.core.TransactionalTaskExecutor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
public class AnnotatedTransactionalTaskExecutor implements TransactionalTaskExecutor {

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void execute(Runnable task) {
        task.run();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRES_NEW)
    public void executeSeparately(Runnable task) {
        task.run();
    }

}
