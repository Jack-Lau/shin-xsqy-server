/*
 * Created 2018-6-25 15:42:12
 */
package cn.com.yting.kxy.web.account;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUsername(String username);

    Account findByUsername(String username);

    @Query("SELECT a.id FROM Account a")
    List<Long> getAllIds();

    @Query("SELECT a.username FROM Account a")
    List<String> getAllUsernames();
}
