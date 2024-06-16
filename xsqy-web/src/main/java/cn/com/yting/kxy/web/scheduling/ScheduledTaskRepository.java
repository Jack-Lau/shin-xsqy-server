/*
 * Created 2018-7-3 19:36:12
 */
package cn.com.yting.kxy.web.scheduling;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

/**
 *
 * @author Azige
 */
public interface ScheduledTaskRepository extends JpaRepository<ScheduledTaskRecord, String> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ScheduledTaskRecord> findById(String id);
}
