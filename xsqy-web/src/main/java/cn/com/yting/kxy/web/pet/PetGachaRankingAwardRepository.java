/*
 * Created 2018-10-12 16:41:25
 */
package cn.com.yting.kxy.web.pet;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

/**
 *
 * @author Azige
 */
public interface PetGachaRankingAwardRepository extends JpaRepository<PetGachaRankingAwardRecord, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    PetGachaRankingAwardRecord findByAccountId(long accountId);
}
