/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.currency.kuaibi;

import java.util.Date;
import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Darkholme
 */
public interface KuaibiDailyRepository extends JpaRepository<KuaibiDailyRecord, Long> {

    @Override
    <S extends KuaibiDailyRecord> S save(S entity);

    @Query("FROM KuaibiDailyRecord")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<KuaibiDailyRecord> findAllForWrite();

    @Query("SELECT r FROM KuaibiDailyRecord r WHERE r.createTime BETWEEN ?1 AND ?2")
    List<KuaibiDailyRecord> findByCreateDateRange(Date startDate, Date endDate);
}
