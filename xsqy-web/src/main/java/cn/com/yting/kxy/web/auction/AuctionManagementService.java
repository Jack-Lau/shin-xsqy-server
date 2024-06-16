/*
 * Created 2018-11-14 11:09:28
 */
package cn.com.yting.kxy.web.auction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.com.yting.kxy.web.KxyWebException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional
public class AuctionManagementService {

    @Autowired
    private CommodityRepository commodityRepository;

    public Commodity createCommodity(Commodity prototype) {
        Commodity commodity = new Commodity();
        commodity.setDefinitionId(prototype.getDefinitionId());
        commodity.setQueueNumber(prototype.getQueueNumber());
        commodity.setCommodityStatus(CommodityStatus.QUEUING);
        return commodityRepository.saveAndFlush(commodity);
    }

    public Commodity modifyCommodity(Commodity reference) {
        Commodity commodity = commodityRepository.findByIdForWrite(reference.getId()).orElseThrow(() -> KxyWebException.notFound("商品不存在"));
        commodity.setDefinitionId(reference.getDefinitionId());
        commodity.setQueueNumber(reference.getQueueNumber());
        return commodity;
    }

    public void deleteCommodity(long id) {
        commodityRepository.deleteById(id);
    }

    public String exportCsv() {
        List<List<Object>> table = new ArrayList<>();
        table.add(Arrays.asList("id", "definitionId", "queueNumber", "commodityStatus", "lastBid", "lastBidderAccountId", "deadline", "broadcastPublished", "delivered"));
        commodityRepository.findByCommodityStatus(CommodityStatus.QUEUING).forEach(commodity -> {
            List<Object> row = new ArrayList<>();
            row.add(commodity.getId());
            row.add(commodity.getDefinitionId());
            row.add(commodity.getQueueNumber());
            row.add(commodity.getCommodityStatus());
            row.add(commodity.getLastBid());
            row.add(commodity.getLastBidderAccountId());
            row.add(commodity.getDeadline());
            row.add(commodity.isBroadcastPublished());
            row.add(commodity.isDelivered());
            table.add(row);
        });
        return table.stream()
            .map(row -> row.stream().map(String::valueOf).collect(Collectors.joining(",")))
            .collect(Collectors.joining("\n"));
    }

    public void importCsv(String text) {
        text = text.replaceAll("\r", "");
        Stream.of(text.split("\n"))
            .skip(1)
            .forEach(line -> {
            String[] fileds = line.split(",");
            Commodity commodity = new Commodity();
            commodity.setDefinitionId(Long.parseLong(fileds[1]));
            commodity.setQueueNumber(Integer.parseInt(fileds[2]));
            if (fileds[0].equals("new")) {
                createCommodity(commodity);
            } else {
                commodity.setId(Long.parseLong(fileds[0]));
                modifyCommodity(commodity);
            }
        });
    }
}
