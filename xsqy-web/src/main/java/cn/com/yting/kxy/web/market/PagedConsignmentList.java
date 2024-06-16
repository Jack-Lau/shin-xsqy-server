/*
 * Created 2018-12-26 11:27:44
 */
package cn.com.yting.kxy.web.market;

import java.util.List;
import java.util.stream.Collectors;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;
import org.springframework.data.domain.Page;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class PagedConsignmentList {

    private List<ConsignmentDetail> consignments;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;

    public static PagedConsignmentList from(Page<ConsignmentDetail> p) {
        return new PagedConsignmentList(
            p.stream().collect(Collectors.toList()),
            p.getNumber(),
            p.getSize(),
            p.getTotalPages(),
            p.getTotalElements()
        );
    }
}
