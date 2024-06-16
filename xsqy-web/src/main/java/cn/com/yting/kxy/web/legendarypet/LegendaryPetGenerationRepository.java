/*
 * Created 2019-1-23 17:41:31
 */
package cn.com.yting.kxy.web.legendarypet;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface LegendaryPetGenerationRepository extends JpaRepository<LegendaryPetGenerationRecord, Long> {

    @Query("FROM LegendaryPetGenerationRecord r WHERE r.definitionId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LegendaryPetGenerationRecord> findByIdForWrite(long definitionId);
}
