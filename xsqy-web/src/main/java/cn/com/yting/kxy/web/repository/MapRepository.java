/*
 * Created 2016-3-9 15:39:21
 */
package cn.com.yting.kxy.web.repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Azige
 */
public abstract class MapRepository<T extends LongId> implements CrudRepository<T, Long>{

    private final ConcurrentHashMap<Long, T> map = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong();

    private void ensureId(T entity){
        if (entity.getId() == null){
            while (true){
                Long id = idSequence.incrementAndGet();
                if (!map.containsKey(id)){
                    entity.setId(id);
                    break;
                }
            }
        }
    }

    private <S extends T> Map<Long, S> iterToMap(Iterable<S> entities){
        return StreamSupport.stream(entities.spliterator(), false)
            .collect(Collectors.toMap(entity -> entity.getId(), Function.identity()));
    }

    protected ConcurrentHashMap<Long, T> getContainerMap(){
        return map;
    }

    @Override
    public synchronized <S extends T> S save(S entity){
        ensureId(entity);
        map.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public synchronized <S extends T> Iterable<S> saveAll(Iterable<S> entities){
        entities.forEach(this::ensureId);
        map.putAll(iterToMap(entities));
        return entities;
    }

    @Override
    public Optional<T> findById(Long id){
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public boolean existsById(Long id){
        return map.containsKey(id);
    }

    @Override
    public Collection<T> findAll(){
        return map.values();
    }

    @Override
    public Collection<T> findAllById(Iterable<Long> ids){
        return StreamSupport.stream(ids.spliterator(), false)
            .map(map::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public long count(){
        return map.size();
    }

    @Override
    public void deleteById(Long id){
        map.remove(id);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        longs.forEach(map::remove);
    }

    @Override
    public void delete(T entity){
        map.remove(entity.getId());
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities){
        Long[] ids = StreamSupport.stream(entities.spliterator(), false)
            .map(entity -> entity.getId())
            .toArray(Long[]::new);
        map.keySet().removeAll(Arrays.asList(ids));
    }

    @Override
    public void deleteAll(){
        map.clear();
    }
}
