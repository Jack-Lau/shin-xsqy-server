/*
 * Created 2018-9-5 18:29:34
 */
package cn.com.yting.kxy.web.proof;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.springframework.security.crypto.codec.Hex;

/**
 *
 * @author Azige
 */
public class ClientProofer {

    private String salt1 = "d62cb2a2a1597f141a61ae93302fb381";
    private String salt2 = "e34cb018b232d8d992b7dfcf8e6b9150";
    private String salt3 = "7084eb1b158c90d066fd8d4ddd4cb2a9";

    public ClientProofer() {
    }

    public boolean proof(String randomValue, String expected) {
        if (randomValue == null || expected == null) {
            return false;
        } else {
            return Arrays.equals(hash(randomValue), Hex.decode(expected));
        }
    }

    public byte[] hash(String randomValue) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest((salt1 + randomValue + salt2 + randomValue + salt3).getBytes(Charset.forName("UTF-8")));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}
