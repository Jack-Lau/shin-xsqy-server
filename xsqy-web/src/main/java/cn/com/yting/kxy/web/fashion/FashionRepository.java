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
public interface FashionRepository extends JpaRepository<Fashion, Long> {

    @Query("SELECT f FROM Fashion f WHERE f.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Fashion> findByIdForWrite(long id);

    List<Fashion> findByAccountId(long accountId);

    Optional<Fashion> findByNftId(long nftId);

}
