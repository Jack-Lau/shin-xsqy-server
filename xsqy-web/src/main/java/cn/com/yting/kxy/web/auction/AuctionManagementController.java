/*
 * Created 2018-11-14 13:03:50
 */
package cn.com.yting.kxy.web.auction;

import java.util.List;

import cn.com.yting.kxy.web.controller.ControllerUtils;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author Azige
 */
@RestController
@RequestMapping("/management/auction")
public class AuctionManagementController {

    @Autowired
    private CommodityRepository commodityRepository;

    @Autowired
    private AuctionManagementService auctionManagementService;

    /*
    @GetMapping("/commodity/{id}")
    public Commodity viewCommodity(@PathVariable("id") long id) {
        return commodityRepository.findById(id).orElseThrow(() -> ControllerUtils.notFoundException());
    }

    @RequestMapping("/commodity/")
    public List<Commodity> viewAllCommodities() {
        return commodityRepository.findAll();
    }

    @PostMapping("/commodity/create")
    public Commodity createCommodity(@RequestBody Commodity prototype) {
        return auctionManagementService.createCommodity(prototype);
    }

    @PostMapping("/commodity/{id}")
    public Commodity modifyCommodity(
        @PathVariable("id") long id,
        @RequestBody Commodity reference
    ) {
        reference.setId(id);
        return auctionManagementService.modifyCommodity(reference);
    }

    @PostMapping("/commodity/{id}/delete")
    public WebMessageWrapper deleteCommodity(@PathVariable("id") long id) {
        auctionManagementService.deleteCommodity(id);
        return WebMessageWrapper.ok();
    }

    @GetMapping(path = "/commodity.csv", produces = "text/csv")
    @ResponseBody
    public String exportCsv() {
        return auctionManagementService.exportCsv();
    }

    @PostMapping(path = "/commodity.csv")
    public RedirectView importCsv(@RequestPart("file") String text) {
        auctionManagementService.importCsv(text);
        return new RedirectView(".");
    }
     */
}
