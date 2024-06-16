/*
 * Created 2019-2-20 15:59:44
 */
package cn.com.yting.kxy.web.topone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnifiedOrderResponse {

    private String prepay_url;
}
