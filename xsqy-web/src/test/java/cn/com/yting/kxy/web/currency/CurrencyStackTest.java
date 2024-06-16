/*
 * Created 2018-7-23 11:20:22
 */
package cn.com.yting.kxy.web.currency;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Azige
 */
public class CurrencyStackTest {

    public CurrencyStackTest() {
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
    public void testEmptyListToText() {
        String text = CurrencyStack.listToText(Collections.emptyList());
        assertThat(text, is(equalTo("")));
    }

    @Test
    public void testSingletonListToText() {
        String text = CurrencyStack.listToText(Collections.singletonList(new CurrencyStack(777, 123456)));
        assertThat(text, is(equalTo("777:123456")));
    }

    @Test
    public void testVanillaListToText() {
        String text = CurrencyStack.listToText(Arrays.asList(
            new CurrencyStack(777, 123),
            new CurrencyStack(888, 234),
            new CurrencyStack(999, 345)
        ));
        assertThat(text, is(equalTo("777:123,888:234,999:345")));
    }

    @Test
    public void testTextToEmptyList() {
        List<CurrencyStack> list = CurrencyStack.listFromText("");
        assertThat(list, is(empty()));
    }

    @Test
    public void testTextToSingletonList() {
        List<CurrencyStack> list = CurrencyStack.listFromText("333:123456");
        assertThat(list, is(equalTo(Collections.singletonList(new CurrencyStack(333, 123456)))));
    }

    @Test
    public void testTextToVanillaList() {
        List<CurrencyStack> list = CurrencyStack.listFromText("111:123,222:234,333:345");
        assertThat(list, is(equalTo(Arrays.asList(
            new CurrencyStack(111, 123),
            new CurrencyStack(222, 234),
            new CurrencyStack(333, 345)
        ))));
    }
}
