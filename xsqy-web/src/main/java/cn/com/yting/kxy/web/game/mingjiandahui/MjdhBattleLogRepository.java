/*
 * Created 2018-12-12 13:06:56
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Azige
 */
public interface MjdhBattleLogRepository extends JpaRepository<MjdhBattleLog, Long> {

    List<MjdhBattleLog> findTop2ByWinnerAccountIdOrderByEventTimeDesc(long accountId);

    List<MjdhBattleLog> findByWinnerAccountIdOrderByEventTimeDesc(long accountId, Pageable pageable);

    List<MjdhBattleLog> findTop2ByLoserAccountIdOrderByEventTimeDesc(long accountId);

    List<MjdhBattleLog> findByLoserAccountIdOrderByEventTimeDesc(long accountId, Pageable pageable);
}
