/*
 * Created 2018-7-26 18:27:44
 */
package cn.com.yting.kxy.web.gift;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface GiftRepository extends JpaRepository<Gift, Long> {

    Gift findByCode(String code);

    @Query("SELECT g.code FROM Gift g WHERE g.giftDefinitionId = ?1")
    List<String> findCodeByDefinitionId(long giftDefinitionId);

    @Query("SELECT COUNT(g) FROM Gift g WHERE g.giftDefinitionId = ?1 AND g.redeemerAccountId = ?2")
    int countByRedeemer(long giftDefinitionId, long redeemerAccountId);
}
