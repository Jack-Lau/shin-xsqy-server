/*
 * Created 2019-2-19 11:20:18
 */
package cn.com.yting.kxy.core.signing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 趣币签名器
 *
 * @author Azige
 */
public class QubiSigner extends Signer {

    private MessageDigest md5;

    public QubiSigner(String key) {
        super(key);
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected synchronized byte[] hash(byte[] bytes) {
        return md5.digest(bytes);
    }
}
