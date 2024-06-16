/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.fashion;

import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Darkholme
 */
public interface FashionDyeRepository extends JpaRepository<FashionDye, Long> {

    @Query("SELECT f FROM FashionDye f WHERE f.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<FashionDye> findByIdForWrite(long id);

    List<FashionDye> findByAccountId(long accountId);

    @Query("SELECT f FROM FashionDye f WHERE f.accountId = ?1 AND f.definitionId = ?2")
    List<FashionDye> findByAccountIdAndDefinitionId(long accountId, long definitionId);

}
