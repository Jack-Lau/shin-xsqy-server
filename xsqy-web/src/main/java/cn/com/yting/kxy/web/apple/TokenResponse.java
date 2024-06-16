// Created 2021/10/9 15:41

package cn.com.yting.kxy.web.apple;

import com.auth0.jwt.JWT;
import lombok.Data;

/**
 * @author Azige
 */
@Data
public class TokenResponse {
    private String access_token;
    private long expires_in;
    private String id_token;
    private String refresh_token;
    private String token_type;

    public String extractUserId() {
        return JWT.decode(id_token).getSubject();
    }
}
