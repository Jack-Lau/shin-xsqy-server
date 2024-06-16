/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.slots;

import java.util.List;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

/**
 *
 * @author Darkholme
 */
public interface SlotsBigPrizeRepository extends JpaRepository<SlotsBigPrize, Long> {

    @Override
    <S extends SlotsBigPrize> S save(S entity);

    @QueryHints({
        @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<SlotsBigPrize> findByAccountId(long accountId);

    @Query("SELECT p"
            + " FROM SlotsBigPrize p"
            + " WHERE p.accountId IN (?1)")
    List<SlotsBigPrize> findByFriendAccountId(List<Long> accountIds);

}
