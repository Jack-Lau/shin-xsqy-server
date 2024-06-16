/*
 * Created 2018-8-3 15:45:40
 */
package cn.com.yting.kxy.web.quest.resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import cn.com.yting.kxy.core.resource.AutoScanResourceContext;

/**
 *
 * @author Azige
 */
public class SequenceQuestLoaderTest {

    public SequenceQuestLoaderTest() {
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
        AutoScanResourceContext context = new AutoScanResourceContext();
        SequenceQuestLoader loader = new SequenceQuestLoader();
        loader.reload(context);
        loader.getAll().values().forEach(System.out::println);
    }

}
