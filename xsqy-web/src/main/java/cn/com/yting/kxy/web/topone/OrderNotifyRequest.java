/*
 * Created 2019-2-20 19:10:15
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
public class OrderNotifyRequest {

    private String appid;
    private String trade_state;
    private String transaction_id;
    private String out_trade_no;
    private String openid;
    private String coin;
    private String amount;
    private String arrival_amount;
    private String fee_amount;
    private String body;
    private String attach;
    private String complete_time;
    private String timestamp;
    private String nonce;
    private String sign;
}
