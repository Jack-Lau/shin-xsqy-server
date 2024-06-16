/*
 * Created 2018-1-5 11:09:42
 */
package cn.com.yting.kxy.core.resetting;

/**
 * 让一个受 Spring 管理的组件实现此接口来处理重置任务
 *
 * @author Azige
 */
public interface ResetTask{

    /**
     * 到达任何重置点的时候都应当首先调用此方法
     *
     * @param resetType 重置类型
     */
    default void anyReset(ResetType resetType){
    }

    /**
     * 每小时重置的任务
     */
    default void hourlyReset(){
    }

    /**
     * 每日重置的任务
     */
    default void dailyReset(){
    }

    /**
     * 每周重置的任务
     */
    default void weeklyReset(){
    }

    /**
     * 每月重置的任务
     */
    default void monthlyReset() {
    }
}
