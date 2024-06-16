/*
 * Created 2018-10-19 18:12:29
 */
package cn.com.yting.kxy.web.game.minearena;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Azige
 */
public interface MineArenaRewardObtainLogRepository extends JpaRepository<MineArenaRewardObtainLog, Long> {

    List<MineArenaRewardObtainLog> findByAccountIdOrderByEventTimeDesc(long accountId, Pageable pageable);
}
