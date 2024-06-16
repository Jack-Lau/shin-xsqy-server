/*
 * Created 2018-8-9 17:25:41
 */
package cn.com.yting.kxy.web.equipment;

import java.util.Collection;
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
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    @Query("SELECT e FROM Equipment e WHERE e.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Equipment> findByIdForWrite(long id);

    @Query("SELECT e FROM Equipment e WHERE e.id IN (?1)")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Equipment> findByIdsForWrite(Collection<Long> ids);

    List<Equipment> findByAccountId(long accountId);

    Optional<Equipment> findByNftId(long nftId);

    @Query("SELECT e FROM Equipment e JOIN PlayerRelation pr"
        + " ON e.id = pr.handEquipmentId"
        + " OR e.id = pr.bodyEquipmentId"
        + " OR e.id = pr.waistEquipmentId"
        + " OR e.id = pr.footEquipmentId"
        + " OR e.id = pr.headEquipmentId"
        + " OR e.id = pr.neckEquipmentId")
    List<Equipment> findArmedEquipments();
}
