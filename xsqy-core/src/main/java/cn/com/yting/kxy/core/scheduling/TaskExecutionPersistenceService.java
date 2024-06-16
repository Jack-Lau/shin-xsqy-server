/*
 * Created 2018-7-3 17:51:36
 */
package cn.com.yting.kxy.core.scheduling;

/**
 *
 * @author Azige
 */
public interface TaskExecutionPersistenceService {

    Long getLastExecution(String taskName);

    void setLastExecution(String taskName, long lastExecution);
}
