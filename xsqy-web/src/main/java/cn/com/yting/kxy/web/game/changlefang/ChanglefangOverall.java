/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.changlefang;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.Value;

/**
 *
 * @author Darkholme
 */
@Value
@WebMessageType
public class ChanglefangOverall {

    private ChanglefangSharedRecord changlefangSharedRecord;
    private ChanglefangRecord changlefangRecord;

}
