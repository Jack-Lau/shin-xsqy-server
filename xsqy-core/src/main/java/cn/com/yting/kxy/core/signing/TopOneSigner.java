/*
 * Created 2019-2-19 15:38:30
 */
package cn.com.yting.kxy.core.signing;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Top One 签名器
 *
 * @author Azige
 */
public class TopOneSigner extends Signer {

    private Mac hmacSha256;

    public TopOneSigner(String key) {
        super(key);
        try {
            hmacSha256 = Mac.getInstance("HmacSHA256");
//            hmacSha256.init(new SecretKeySpec(Hex.decodeHex(key), "HmacSHA256"));
            hmacSha256.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
//        } catch (NoSuchAlgorithmException | DecoderException | InvalidKeyException ex) {
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected synchronized byte[] hash(byte[] bytes) {
        return hmacSha256.doFinal(bytes);
    }
}
