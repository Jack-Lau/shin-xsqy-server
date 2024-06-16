/*
 * Created 2019-2-19 15:09:22
 */
package cn.com.yting.kxy.core.signing;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Azige
 */
public class SignerTest {

    public SignerTest() {
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
    public void testQubiSigner() {
        QubiSigner signer = new QubiSigner("192006250b4c09247ec02edce69f6a2d");
        String sign = signer.start()
            .parameter("app_id", "wxd930ea5d5a258f4f")
            .parameter("nonce", "ibuaiVcKdpRxkhJA")
            .sign();
        assertThat(sign, is("D6DDFE75BF4920E93D8B828C41741953"));
    }

    @Test
    public void testTopOneSigner() {
        TopOneSigner signer = new TopOneSigner("192006250b4c09247ec02edce69f6a2d");
        String sign = signer.start()
            .parameter("appid", "d930ea5d5a258f4f")
            .parameter("nonce", "ibuaiVcKdpRxkhJA")
            .parameter("code", "90FD6257D693A078E1C3E4B")
            .parameter("grant_type", "authorization_code")
            .parameter("timestamp", "1541941677")
            .sign();
        assertThat(sign, is("048CDE11C3D3AE9F9BA2CF6D672FC03540A3F31EFFD4BFEAA9AE2ABDB4A5EB22"));
    }
}
