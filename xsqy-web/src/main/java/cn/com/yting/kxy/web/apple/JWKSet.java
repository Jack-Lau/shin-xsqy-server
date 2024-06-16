// Created 2021/10/9 15:08

package cn.com.yting.kxy.web.apple;

import lombok.Data;

import java.util.List;

/**
 * @author Azige
 */
@Data
public class JWKSet {

    private List<Keys> keys;

    @Data
    public static class Keys {
        private String alg;
        private String e;
        private String kid;
        private String kty;
        private String n;
        private String use;
    }
}
