/*
 * Created 2017-7-26 12:47:58
 */
package cn.com.yting.kxy.core.signing;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Azige
 */
public abstract class Signer {

    private static final Logger LOG = LoggerFactory.getLogger(Signer.class);
    private final String key;

    public Signer(String key) {
        this.key = key;
    }

    protected String getKey() {
        return key;
    }

    public Signing start() {
        return new Signing();
    }

    /**
     * 将参数键值对转换为文本形式，通常是 "key=value"
     *
     * @param parameterEntry
     * @return
     */
    protected String mapParameterToString(Map.Entry<String, String> parameterEntry) {
        return parameterEntry.getKey() + "=" + parameterEntry.getValue();
    }

    /**
     * 排序参数的方式，通常是自然排序
     * 如果为 null 则不排序
     *
     * @return
     */
    protected Comparator<String> parameterComparator() {
        return Comparator.naturalOrder();
    }

    /**
     * 连接参数的方式，例如 "pair1&pair2"
     *
     * @param pair1
     * @param pair2
     * @return
     */
    protected String accumulateParameter(String pair1, String pair2) {
        return pair1 + "&" + pair2;
    }

    /**
     * 将密钥拼接到整理完成的参数字符串上，通常是将 key 也当作参数拼到最后
     *
     * @param parameterString
     * @return
     */
    protected String appendKeyTo(String parameterString) {
        if (parameterString.equals("")) {
            return "key=" + getKey();
        } else {
            return parameterString + "&key=" + getKey();
        }
    }

    /**
     * 对构建完的字符串进行消息摘要提取
     *
     * @param bytes
     * @return
     */
    protected abstract byte[] hash(byte[] bytes);

    /**
     * 将摘要提取后的字节转换为文本形式，通常是大写的 Hex 编码
     *
     * @param bytes
     * @return
     */
    protected String convertDigestBytes(byte[] bytes) {
        return Hex.encodeHexString(bytes, false);
    }

    public class Signing {

        private Map<String, String> parameterMap = new LinkedHashMap<>();

        /**
         * value 可以为null或"" 计算时会自动忽略
         *
         * @param key
         * @param value
         * @return
         */
        public Signing parameter(String key, Object value) {
            if (value != null) {
                parameterMap.put(key, String.valueOf(value));
            }
            return this;
        }

        public Signing parameters(Map<String, String> parameterMap) {
            this.parameterMap.putAll(parameterMap);
            return this;
        }

        public String sign() {
            Stream<String> stream = parameterMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().equals(""))
                .map(Signer.this::mapParameterToString);
            Comparator<String> comparator = parameterComparator();
            if (comparator != null) {
                stream = stream.sorted(parameterComparator());
            }
            String parameterString = stream
                .reduce(Signer.this::accumulateParameter)
                .orElse("");
            String textToDigest = appendKeyTo(parameterString);
            byte[] bytes = hash(textToDigest.getBytes(Charset.forName("UTF-8")));
            String sign = convertDigestBytes(bytes);
            if (LOG.isDebugEnabled()) {
                LOG.debug("参数字符串：{}", parameterString);
                LOG.debug("待提取字符串：{}", textToDigest);
                LOG.debug("签名字符串：{}", sign);
            }
            return sign;
        }

        public boolean verify(String sign) {
            return Objects.equals(sign, sign());
        }
    }
}
