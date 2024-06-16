/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.treasureBowl;

import java.util.List;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

/**
 *
 * @author Administrator
 */
public interface TreasureBowlAttendRepository extends JpaRepository<TreasureBowlAttendRecord, Long> {

    @Override
    <S extends TreasureBowlAttendRecord> S save(S entity);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<TreasureBowlAttendRecord> findByAccountId(long accountId);

    @Query("SELECT e FROM TreasureBowlAttendRecord e WHERE e.treasureBowlId = ?1 ORDER BY totalContribution DESC")
    List<TreasureBowlAttendRecord> findByTreasureBowlId(long treasureBowlId);

    @Query("SELECT e FROM TreasureBowlAttendRecord e WHERE e.accountId = ?1 AND e.treasureBowlId = ?2")
    TreasureBowlAttendRecord findByAccountIdAndTreasureBowlId(long accountId, long treasureBowlId);

}
