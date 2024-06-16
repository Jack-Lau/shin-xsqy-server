/*
 * Created 2017-7-19 12:22:48
 */
package cn.com.yting.kxy.core.scheduling;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.TransactionalTaskExecutor;
import io.reactivex.Completable;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodIntrospector.MetadataLookup;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;

/**
 *
 * @author Azige
 */
public class ScheduledTaskManager implements SmartLifecycle, ApplicationContextAware {

    private static final int RETRY_LIMIT = 5;
    private static final long RETRY_WAIT_TIME = 3000;
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTaskManager.class);

    private final TaskScheduler taskScheduler;
    private final TaskExecutionPersistenceService taskExecutionPersistenceService;
    private final TransactionalTaskExecutor transactionalTaskExecutor;
    private final TimeProvider timeProvider;
    private ApplicationContext applicationContext;
    private final Object lock = new Object();

    private Map<String, ScheduledTask> taskMap = new HashMap<>();
    private Map<String, ScheduledFuture<?>> futureMap = new HashMap<>();
    private boolean running = false;
    /**
     * 指定此管理器是否确实启用。
     * 如果没有启用，向此管理器注册了定时任务后并不会提交到 taskScheduler
     */
    @Getter
    @Setter
    private boolean enabled = true;

    public ScheduledTaskManager(
        TaskScheduler taskScheduler,
        TaskExecutionPersistenceService taskExecutionPersistenceService,
        TransactionalTaskExecutor transactionalTaskExecutor,
        TimeProvider timeProvider
    ) {
        this.taskScheduler = taskScheduler;
        this.taskExecutionPersistenceService = taskExecutionPersistenceService;
        this.transactionalTaskExecutor = transactionalTaskExecutor;
        this.timeProvider = timeProvider;
    }

    /**
     * 相当于 {@code registerTask(name, cronExpress, task, false) }
     *
     * @param name        任务的名字，建议使用 {类全名}.{任务名}
     * @param cronExpress 计划任务的 cron 表达式
     * @param task        需要执行的任务
     */
    public void registerTask(String name, String cronExpress, Runnable task) {
        registerTask(name, cronExpress, task, false);
    }

    /**
     * 注册一个计划任务，如果此任务曾经注册过，且在注册或最后一次执行后曾经错过下一次执行点，
     * 或是新任务且指定 executeIfnew 为 true，在应用程序初始化完成后会立即执行一次。
     * 此外，因为在 Spring Bean 中调用同一个类的方法可能不会经过 Spring 的代理，建议在另一个
     * 组件中引用目标组件并注册调用目标组件上的方法的任务。
     *
     * @param name         任务的名字，建议使用 {类全名}.{任务名}
     * @param cronExpress  计划任务的 cron 表达式
     * @param task         需要执行的任务
     * @param executeIfnew 指定对于没有记录过的新任务，是否立即执行一次
     */
    public void registerTask(String name, String cronExpress, Runnable task, boolean executeIfnew) {
        synchronized (lock) {
            ScheduledTask scheduledTask = new ScheduledTask(name, cronExpress, task, executeIfnew);
            taskMap.put(name, scheduledTask);
            if (running) {
                registerTaskToScheduler(scheduledTask);
            }
        }
    }

    private void executeTask(ScheduledTask scheduledTask) {
        LOG.info("开始执行计划任务 {}", scheduledTask.getName());
        Completable.fromRunnable(() -> {
            transactionalTaskExecutor.execute(() -> {
                Long lastExecution = taskExecutionPersistenceService.getLastExecution(scheduledTask.getName());
                if (lastExecution == null) {
                    LOG.info("名称为 {} 的计划任务记录不存在，将为其创建记录", scheduledTask.getName());
                }

                scheduledTask.getTask().run();

                taskExecutionPersistenceService.setLastExecution(scheduledTask.getName(), timeProvider.currentTime());
            });
        })
            .retry((count, ex) -> {
                if (count < RETRY_LIMIT) {
                    LOG.warn("计划任务 {} 在执行中发生异常，将要重试，第 {} 次", scheduledTask.getName(), count, ex);
                    Thread.sleep(RETRY_WAIT_TIME);
                    return true;
                } else {
                    LOG.error("计划任务 {} 在执行中发生异常，不再重试", scheduledTask.getName(), ex);
                    return false;
                }
            })
            .blockingAwait();
    }

    private void registerTaskToScheduler(ScheduledTask scheduledTask) {
        if (!enabled) {
            return;
        }
        Runnable wrappedTask = () -> executeTask(scheduledTask);
        Long lastExecution = taskExecutionPersistenceService.getLastExecution(scheduledTask.getName());
        if (lastExecution != null) {
            Date lastExecutionDate = new Date(lastExecution);
            Date nextExecutionDate = new CronSequenceGenerator(scheduledTask.getCronExpression()).next(lastExecutionDate);
            if (nextExecutionDate.getTime() <= timeProvider.currentTime()) {
                LOG.info("计划任务 {} 曾错过执行时机，现在将执行任务。上次执行时间={}", scheduledTask.getName(), lastExecutionDate);
                wrappedTask.run();
            }
        } else if (scheduledTask.isExecuteIfNew()) {
            LOG.info("将立即执行新创建的计划任务 {}", scheduledTask.getName());
            wrappedTask.run();
        }
        ScheduledFuture<?> future = taskScheduler.schedule(wrappedTask, new CronTrigger(scheduledTask.getCronExpression()));
        LOG.info("已注册名为 {} 的计划任务，触发表达式为 '{}'", scheduledTask.getName(), scheduledTask.getCronExpression());
        future = futureMap.put(scheduledTask.getName(), future);
        if (future != null) {
            LOG.warn("名称为 {} 的任务已存在，旧任务将被取消", scheduledTask.getName());
            future.cancel(true);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private void scanAnnotatedMethods() {
        Arrays.stream(applicationContext.getBeanDefinitionNames())
            .map(applicationContext::getBean)
            .forEach(bean -> MethodIntrospector.selectMethods(
                bean.getClass(),
                (MetadataLookup<RegisterScheduledTask>) (method -> AnnotationUtils.findAnnotation(method, RegisterScheduledTask.class))
            ).forEach((method, anno) -> {
                String name;
                if (!anno.fullName().equals("")) {
                    name = anno.fullName();
                } else if (!anno.name().equals("")) {
                    name = anno.name();
                } else {
                    name = AopUtils.getTargetClass(bean).getName() + "." + method.getName();
                }
                registerTask(name, anno.cronExpression(), () -> {
                    try {
                        method.invoke(bean);
                    } catch (IllegalAccessException | IllegalArgumentException ex) {
                        throw new RuntimeException(ex);
                    } catch (InvocationTargetException ex) {
                        Throwable casue = ex.getTargetException();
                        if (casue instanceof RuntimeException) {
                            throw (RuntimeException) casue;
                        } else {
                            throw new RuntimeException(ex);
                        }
                    }
                }, anno.executeIfNew());
            }));
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void start() {
        synchronized (lock) {
            if (running) {
                return;
            }
            if (applicationContext != null) {
                scanAnnotatedMethods();
            }
            running = true;

            if (!enabled) {
                LOG.warn("计划任务管理器并未启用，所有计划任务不会提交到 taskScheduler");
            } else {
                taskMap.values().forEach(this::registerTaskToScheduler);
            }
        }
    }

    @Override
    public void stop() {
        synchronized (lock) {
            if (!running) {
                return;
            }
            running = false;

            futureMap.values().forEach(it -> it.cancel(true));
            futureMap.clear();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
