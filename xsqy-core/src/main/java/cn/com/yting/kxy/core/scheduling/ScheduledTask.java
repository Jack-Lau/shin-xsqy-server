/*
 * Created 2017-7-19 12:32:37
 */
package cn.com.yting.kxy.core.scheduling;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class ScheduledTask{

    private String name;
    private String cronExpression;
    private Runnable task;
    private boolean executeIfNew;
}
