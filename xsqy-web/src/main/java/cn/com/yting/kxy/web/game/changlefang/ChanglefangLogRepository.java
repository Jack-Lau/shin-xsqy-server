/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.changlefang;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Darkholme
 */
public interface ChanglefangLogRepository extends JpaRepository<ChanglefangLog, Long> {

    List<ChanglefangLog> findByAccountId(long accountId);

}
