/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.changlefang;

import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

/**
 *
 * @author Darkholme
 */
public interface ChanglefangRepository extends JpaRepository<ChanglefangRecord, Long> {

    @Override
    <S extends ChanglefangRecord> S save(S entity);

    @Query("SELECT c FROM ChanglefangRecord c WHERE c.accountId = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ChanglefangRecord> findByAccountIdForWrite(long accountId);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    ChanglefangRecord findByAccountId(long accountId);

}
