/*
 * Created 2019-2-21 12:40:16
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
public class OrderQueryResponse {

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
}
