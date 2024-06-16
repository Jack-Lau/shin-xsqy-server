/*
 * Created 2018-12-12 13:12:53
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface MjdhSeasonRepository extends JpaRepository<MjdhSeason, Long> {

    @Query("SELECT r FROM MjdhSeason r WHERE r.id = ?1")
    Optional<MjdhSeason> findByIdForWrite(long id);

    Optional<MjdhSeason> findTopByOrderByIdDesc();
}
