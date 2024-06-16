/*
 * Created 2018-10-10 17:59:49
 */
package cn.com.yting.kxy.web.pet;

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
public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query("SELECT p FROM Pet p WHERE p.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Pet> findByIdForWrite(long id);

    @Query("SELECT p FROM Pet p WHERE p.id in (?1)")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Pet> findByIdsForWrite(Collection<Long> ids);

    @Query("SELECT p.id FROM Pet p WHERE p.accountId = ?1 ORDER BY p.sortingIndex DESC")
    List<Long> findIdsByAccountId(long accountId);

    @Query("SELECT p.id FROM Pet p WHERE p.accountId = ?1 AND p.legendary = TRUE ORDER BY p.sortingIndex DESC")
    List<Long> findLegendaryIdsByAccountId(long accountId);

    @Query("SELECT p.id FROM Pet p WHERE p.accountId = ?1 AND p.candidateAbilitiesText != ''")
    List<Long> findByAccountIdAndCandidateAbilitiesTextNotEmpty(long accountId);

    @Query("SELECT p FROM Pet p JOIN PlayerRelation pr"
        + " ON p.id = pr.battlePetId1"
        + " OR p.id = pr.battlePetId2"
        + " OR p.id = pr.battlePetId3")
    List<Pet> findInBattlePets();

    Optional<Pet> findByNftId(long nftId);

    List<Pet> findByAccountId(long accountId);
}
