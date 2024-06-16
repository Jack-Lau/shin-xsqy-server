/*
 * Created 2017-7-19 17:58:35
 */
package cn.com.yting.kxy.core.resetting;

import java.util.List;
import java.util.function.Consumer;

import cn.com.yting.kxy.core.TransactionalTaskExecutor;
import cn.com.yting.kxy.core.scheduling.ScheduledTaskManager;
import io.reactivex.Completable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * @author Azige
 */
public class ResettingManager implements InitializingBean {

    private static final int RETRY_LIMIT = 5;
    private static final long RETRY_WAIT_TIME = 3000;
    private static final Logger LOG = LoggerFactory.getLogger(ResettingManager.class);

    private final ScheduledTaskManager scheduledTaskManager;
    private final List<ResetTask> resetTasks;
    private final TransactionalTaskExecutor transactionalTaskExecutor;

    public ResettingManager(
        ScheduledTaskManager scheduledTaskManager,
        List<ResetTask> resetTasks,
        TransactionalTaskExecutor transactionalTaskExecutor
    ) {
        this.scheduledTaskManager = scheduledTaskManager;
        this.resetTasks = resetTasks;
        this.transactionalTaskExecutor = transactionalTaskExecutor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String prefix = ResettingManager.class.getName() + ".";
        scheduledTaskManager.registerTask(prefix + "hourlyReset", ResetConstants.CRON_HOURLY, () -> {
            LOG.info("开始每小时重置");
            doReset(ResetType.HOURLY, ResetTask::hourlyReset);
        });
        scheduledTaskManager.registerTask(prefix + "dailyReset", ResetConstants.CRON_DAILY, () -> {
            LOG.info("开始每日重置");
            doReset(ResetType.DAILY, ResetTask::dailyReset);
        });
        scheduledTaskManager.registerTask(prefix + "weeklyReset", ResetConstants.CRON_WEEKLY, () -> {
            LOG.info("开始每周重置");
            doReset(ResetType.WEEKLY, ResetTask::weeklyReset);
        });
        scheduledTaskManager.registerTask(prefix + "monthlyReset", ResetConstants.CRON_MONTHLY, () -> {
            LOG.info("开始每月重置");
            doReset(ResetType.MONTHLY, ResetTask::monthlyReset);
        });
    }

    private void doReset(ResetType resetType, Consumer<ResetTask> resetAction) {
        resetTasks.forEach(task -> {
            Completable.fromRunnable(() -> {
                transactionalTaskExecutor.executeSeparately(() -> {
                    task.anyReset(resetType);
                    resetAction.accept(task);
                });
            })
                .retry((count, ex) -> {
                    if (count < RETRY_LIMIT) {
                        LOG.warn("重置任务在执行中发生异常，类={}，将要重试，第 {} 次", task.getClass(), count, ex);
                        Thread.sleep(RETRY_WAIT_TIME);
                        return true;
                    } else {
                        LOG.error("重置任务在执行中发生异常，类={}，不再重试", task.getClass(), ex);
                        return false;
                    }
                })
                .onErrorComplete()
                .blockingAwait();
        });
    }
}
