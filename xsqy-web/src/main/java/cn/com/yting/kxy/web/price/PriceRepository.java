/*
 * Created 2018-9-20 10:46:41
 */
package cn.com.yting.kxy.web.price;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Azige
 */
public interface PriceRepository extends JpaRepository<PriceRecord, Long> {

    @Query("SELECT r FROM PriceRecord r WHERE r.id = ?1")
    Optional<PriceRecord> findByIdForWrite(long id);
}
