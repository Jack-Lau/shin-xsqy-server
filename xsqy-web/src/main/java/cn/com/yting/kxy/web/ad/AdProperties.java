// Created 2021/9/1 16:06

package cn.com.yting.kxy.web.ad;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Azige
 */
@Component
@ConfigurationProperties("kxy.web.ad")
@Data
public class AdProperties {
    private Map<String, String> secrets;
}
