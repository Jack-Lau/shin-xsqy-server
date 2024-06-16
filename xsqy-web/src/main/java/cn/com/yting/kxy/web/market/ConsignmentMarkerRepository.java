/*
 * Created 2018-12-21 16:30:23
 */
package cn.com.yting.kxy.web.market;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Azige
 */
public interface ConsignmentMarkerRepository extends JpaRepository<ConsignmentMarker, ConsignmentMarker.PK> {

    default Optional<ConsignmentMarker> findById(long accountId, long consignmentId) {
        return findById(new ConsignmentMarker.PK(accountId, consignmentId));
    }

    List<ConsignmentMarker> findByAccountId(long accountId);
}
