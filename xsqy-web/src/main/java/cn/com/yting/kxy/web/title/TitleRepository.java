/*
 * Created 2018-8-9 16:39:47
 */
package cn.com.yting.kxy.web.title;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface TitleRepository extends JpaRepository<Title, Long> {

    @Query("SELECT r FROM Title r WHERE r.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Title> findByIdForWrite(long id);

    Optional<Title> findByAccountIdAndDefinitionId(long accountId, long definitionId);

    List<Title> findByAccountId(long accountId);
}
