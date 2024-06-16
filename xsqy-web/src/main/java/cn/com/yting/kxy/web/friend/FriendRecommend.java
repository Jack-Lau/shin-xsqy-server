/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.friend;

import cn.com.yting.kxy.web.message.WebMessageType;
import java.util.List;
import lombok.Value;

/**
 *
 * @author Darkholme
 */
@Value
@WebMessageType
public class FriendRecommend {

    private List<Friend> rankingList;
    private List<Friend> fcList;
    private List<Friend> lvList;

}
