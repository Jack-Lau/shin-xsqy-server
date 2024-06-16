/*
 * Created 2018-11-2 15:56:02
 */
package cn.com.yting.kxy.web.awardpool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import cn.com.yting.kxy.core.random.pool.PoolSelector;
import cn.com.yting.kxy.core.random.pool.PoolSelectorResult;
import cn.com.yting.kxy.core.random.pool.PoolValueHolder;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resetting.ResetType;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class AwardPoolService implements ResetTask {

    @Autowired
    private AwardPoolRepository awardPoolRepository;
    @Autowired
    private AwardPoolPlayerRepository awardPoolPlayerRepository;

    private final Map<Long, Supplier<PoolSelector>> poolSelectorMap = new HashMap<>();

    public void initPools() {
        AwardPoolConstants.CONFIG_MAP.forEach((poolId, config) -> {
            if (!awardPoolRepository.findByIdForWrite(poolId).isPresent()) {
                AwardPoolRecord record = new AwardPoolRecord();
                record.setId(poolId);
                record.setPoolValue(config.getInitPublicPoolValue());
                awardPoolRepository.save(record);
            }
        });
    }

    public void registerPoolSelector(long poolId, Supplier<PoolSelector> poolSelectorSupplier) {
        poolSelectorMap.put(poolId, poolSelectorSupplier);
    }

    public Collection<PoolSelectorResult> select(long poolId, long accountId) {
        AwardPoolRecord awardPoolRecord = awardPoolRepository.findByIdForWrite(poolId).get();
        AwardPoolPlayerRecord awardPoolPlayerRecord = awardPoolPlayerRepository.findOrCreateById(poolId, accountId, AwardPoolConstants.CONFIG_MAP.get(poolId).getInitPersonalPoolValue());
        PoolValueHolder valueHolder = new PoolValueHolder(awardPoolRecord.getPoolValue(), awardPoolPlayerRecord.getPoolValue());
        Map<Long, PoolValueHolder> poolValueMap = ImmutableMap.of(CurrencyConstants.ID_毫仙石, valueHolder);
        Collection<PoolSelectorResult> results = poolSelectorMap.get(poolId).get().get(poolValueMap);
        awardPoolRecord.setPoolValue(valueHolder.getRemainTotalPoolAmount());
        awardPoolPlayerRecord.setPoolValue(valueHolder.getRemainPersonalPoolAmount());
        return results;
    }

    @Override
    public void anyReset(ResetType resetType) {
        AwardPoolConstants.CONFIG_MAP.forEach((poolId, config) -> {
            if (config.getPublicPoolResetType().equals(resetType)) {
                awardPoolRepository.findById(poolId).ifPresent(record -> {
                    record.setPoolValue(config.getInitPublicPoolValue());
                });
            }
            if (config.getPersonalPoolResetType().equals(resetType)) {
                awardPoolPlayerRepository.resetPoolValueByPoolId(poolId, config.getInitPersonalPoolValue());
            }
        });
    }
}
