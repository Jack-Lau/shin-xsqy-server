/*
 * Created 2018-7-3 19:07:36
 */
package cn.com.yting.kxy.web.scheduling;

import java.util.Date;

import cn.com.yting.kxy.core.scheduling.TaskExecutionPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Component
@Transactional
public class ScheduledTaskService implements TaskExecutionPersistenceService {

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Override
    public Long getLastExecution(String taskName) {
        return scheduledTaskRepository.findById(taskName)
            .map(it -> it.getLastExecution().getTime())
            .orElse(null);
    }

    @Override
    public void setLastExecution(String taskName, long lastExecution) {
        ScheduledTaskRecord record = scheduledTaskRepository.findById(taskName)
            .orElse(new ScheduledTaskRecord(taskName));
        record.setLastExecution(new Date(lastExecution));
        scheduledTaskRepository.saveAndFlush(record);
    }
}
