/*
 * Created 2018-8-11 17:40:46
 */
package cn.com.yting.kxy.web.award;

import java.util.List;

import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.equipment.Equipment;
import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.pet.Pet;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class AwardResult {

    private List<CurrencyStack> currencyStacks;
    private List<Equipment> equipments;
    private List<Pet> pets;

}
