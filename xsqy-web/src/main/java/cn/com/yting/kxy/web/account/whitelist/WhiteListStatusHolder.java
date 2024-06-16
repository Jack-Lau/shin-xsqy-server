/*
 * Created 2018-9-25 11:32:15
 */
package cn.com.yting.kxy.web.account.whitelist;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
@Data
public class WhiteListStatusHolder {

    @Value("${kxy.web.whiteList.enable}")
    private boolean enabled;
}
