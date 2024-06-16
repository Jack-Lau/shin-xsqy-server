/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.drug;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import cn.com.yting.kxy.web.repository.MapRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Administrator
 */
@Repository
public class DrugRepository extends MapRepository<DrugRecord> {


    @Query("SELECT e FROM DrugRecord e WHERE e.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public List<DrugRecord> findByAccountIdForWrite(long accountId) {
        return findByAccountId(accountId);
    }

    @Query("SELECT e FROM DrugRecord e WHERE e.accountId = ?1 AND e.drugId = ?2")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public List<DrugRecord> findByAccountIdAndDrugIdForWrite(long accountId, long drugId) {
        return getContainerMap().values().stream()
            .filter(it -> it.getAccountId() == accountId && it.getDrugId() == drugId)
            .collect(Collectors.toList());
    }

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    public List<DrugRecord> findByAccountId(long accountId) {
        return getContainerMap().values().stream()
            .filter(it -> it.getAccountId() == accountId)
            .collect(Collectors.toList());
    }

}
