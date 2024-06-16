/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.player;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Darkholme
 */
public interface PlayerNameUsedRepository extends JpaRepository<PlayerNameUsed, Long> {

    List<PlayerNameUsed> findByAccountId(long accountId);

    Optional<PlayerNameUsed> findByUsedName(String usedName);

}
