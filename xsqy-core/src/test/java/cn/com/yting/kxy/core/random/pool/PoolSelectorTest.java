/*
 * Created 2018-7-28 11:41:40
 */
package cn.com.yting.kxy.core.random.pool;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Map;

import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.core.random.RandomSelectType;
import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Azige
 */
public class PoolSelectorTest {

    public PoolSelectorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
        /*
        0.9626044800803012
        0.8822720345293212
        0.5667874819674239
        0.6602258059170043
        0.4995251957425837
        0.9362192260502197
        0.5549897676891352
        0.7417535471126616
        0.2954958984697259
        0.22131418687211857
         */
        RandomProvider.getRandom().setSeed(1511037822);
        PoolSelector selector = PoolSelector.builder()
            .add(new PoolSelectorElement(150, 10, 0.2))
            .add(new PoolSelectorElement(150, 50, 0.2))
            .add(new PoolSelectorElement(150, 100, 0.2))
            .add(new PoolSelectorElement(151, 50, 0.2))
            .add(new PoolSelectorElement(151, 100, 0.2))
            .build(RandomSelectType.DEPENDENT);
        Map<Long, PoolValueHolder> poolValueMap = ImmutableMap.of(150L, new PoolValueHolder(120, Long.MAX_VALUE));

        Collection<PoolSelectorResult> resultList;
        resultList = selector.get(poolValueMap);
        assertThat(resultList, hasSize(1));
        assertThat(resultList.iterator().next(), is(new PoolSelectorResult(151, 100)));
        assertThat(poolValueMap.get(150L).getRemainTotalPoolAmount(), is(120L));

        resultList = selector.get(poolValueMap);
        assertThat(resultList, hasSize(1));
        assertThat(resultList.iterator().next(), is(new PoolSelectorResult(151, 100)));
        assertThat(poolValueMap.get(150L).getRemainTotalPoolAmount(), is(120L));

        resultList = selector.get(poolValueMap);
        assertThat(resultList, hasSize(1));
        assertThat(resultList.iterator().next(), is(new PoolSelectorResult(150, 100)));
        assertThat(poolValueMap.get(150L).getRemainTotalPoolAmount(), is(20L));

        resultList = selector.get(poolValueMap);
        assertThat(resultList, hasSize(1));
        assertThat(resultList.iterator().next(), is(new PoolSelectorResult(151, 50)));
        assertThat(poolValueMap.get(150L).getRemainTotalPoolAmount(), is(20L));

        resultList = selector.get(poolValueMap);
        assertThat(resultList, hasSize(1));
        assertThat(resultList.iterator().next(), is(new PoolSelectorResult(151, 50)));
        assertThat(poolValueMap.get(150L).getRemainTotalPoolAmount(), is(20L));

        resultList = selector.get(poolValueMap);
        assertThat(resultList, hasSize(1));
        assertThat(resultList.iterator().next(), is(new PoolSelectorResult(151, 100)));
        assertThat(poolValueMap.get(150L).getRemainTotalPoolAmount(), is(20L));

        resultList = selector.get(poolValueMap);
        assertThat(resultList, hasSize(1));
        assertThat(resultList.iterator().next(), is(new PoolSelectorResult(151, 50)));
        assertThat(poolValueMap.get(150L).getRemainTotalPoolAmount(), is(20L));

        resultList = selector.get(poolValueMap);
        assertThat(resultList, hasSize(1));
        assertThat(resultList.iterator().next(), is(new PoolSelectorResult(151, 100)));
        assertThat(poolValueMap.get(150L).getRemainTotalPoolAmount(), is(20L));

        resultList = selector.get(poolValueMap);
        assertThat(resultList, hasSize(1));
        assertThat(resultList.iterator().next(), is(new PoolSelectorResult(150, 10)));
        assertThat(poolValueMap.get(150L).getRemainTotalPoolAmount(), is(10L));

        resultList = selector.get(poolValueMap);
        assertThat(resultList, hasSize(1));
        assertThat(resultList.iterator().next(), is(new PoolSelectorResult(150, 10)));
        assertThat(poolValueMap.get(150L).getRemainTotalPoolAmount(), is(0L));
    }

}
