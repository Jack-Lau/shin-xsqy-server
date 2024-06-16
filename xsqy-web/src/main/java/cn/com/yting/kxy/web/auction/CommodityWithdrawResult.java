/*
 * Created 2018-11-17 1:30:34
 */
package cn.com.yting.kxy.web.auction;

import java.util.List;

import cn.com.yting.kxy.web.equipment.Equipment;
import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.pet.Pet;
import cn.com.yting.kxy.web.title.Title;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class CommodityWithdrawResult {

    private List<Equipment> equipments;
    private List<Pet> pets;
    private List<Title> titles;
}
