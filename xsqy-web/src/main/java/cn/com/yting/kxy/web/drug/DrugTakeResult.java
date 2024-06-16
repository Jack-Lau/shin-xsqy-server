/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.drug;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Administrator
 */
@Value
@WebMessageType
public class DrugTakeResult {
    
    private long type;
    private String name;
    private String attr_name_1;
    private double attr_value_1;
    private String attr_name_2;
    private double attr_value_2;
    
}
