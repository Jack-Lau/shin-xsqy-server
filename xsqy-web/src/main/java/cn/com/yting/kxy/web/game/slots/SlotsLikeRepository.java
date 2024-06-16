/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.slots;

import java.util.List;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

/**
 *
 * @author Darkholme
 */
public interface SlotsLikeRepository extends JpaRepository<SlotsLike, Long> {

    @Override
    <S extends SlotsLike> S save(S entity);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<SlotsLike> findByReceiverId(long receiverId);

}
