/*
 * Created 2018-9-3 12:10:27
 */
package cn.com.yting.kxy.web.repository;

import java.util.Optional;
import java.util.function.Supplier;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

/**
 *
 * @author Azige
 */
@NoRepositoryBean
public interface SingleRecordRepository<T extends LongId> extends JpaRepository<T, Long> {

    long DEFAULT_ID = 1L;

    @Query("SELECT e FROM #{#entityName} e WHERE e.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<T> findByIdForWrite(Long id);

    default T getTheRecord() {
        return findById(DEFAULT_ID).get();
    }

    default T getTheRecordForWrite() {
        return findByIdForWrite(DEFAULT_ID).get();
    }

    /**
     *
     * @param defaultInstance
     * @deprecated 使用 {@link #init(java.util.function.Supplier)}
     */
    @Deprecated
    default void init(T defaultInstance) {
        save(findById(DEFAULT_ID).orElseGet(() -> {
            defaultInstance.setId(DEFAULT_ID);
            return defaultInstance;
        }));
    }

    default void init(Supplier<T> supplier) {
        save(findById(DEFAULT_ID).orElseGet(() -> {
            T record = supplier.get();
            record.setId(DEFAULT_ID);
            return record;
        }));
    }
}
