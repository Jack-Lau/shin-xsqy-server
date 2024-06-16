/*
 * Created 2018-7-3 17:57:01
 */
package cn.com.yting.kxy.core;

/**
 * 将一个任务包装在事务中执行的执行器接口
 *
 * @author Azige
 */
public interface TransactionalTaskExecutor {

    /**
     * 执行一个任务，使用当前或新的事务
     *
     * @param task 要执行的任务
     */
    void execute(Runnable task);

    /**
     * 执行一个任务，使用单独的新事务
     *
     * @param task       要执行的任务
     */
    void executeSeparately(Runnable task);
}
