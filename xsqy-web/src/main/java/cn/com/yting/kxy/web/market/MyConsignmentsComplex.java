/*
 * Created 2018-12-25 16:42:24
 */
package cn.com.yting.kxy.web.market;

import java.util.List;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class MyConsignmentsComplex {

    private List<ConsignmentDetail> onSaleConsignments;
    private List<ConsignmentDetail> suspendedConsignments;
    private List<ConsignmentDetail> goodsObtainableConsignments;
    private List<ConsignmentDetail> paymentObtainableConsignments;
    private List<ConsignmentDetail> archiveConsignments;
}
