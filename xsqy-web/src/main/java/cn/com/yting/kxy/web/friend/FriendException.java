/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.friend;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Darkholme
 */
public class FriendException extends KxyWebException {

    public static final int EC_PLAYER_NOT_EXIST = 2100;
    public static final int EC_APPLY_ALREADY_EXIST = 2101;
    public static final int EC_APPLY_NOT_EXIST = 2102;
    public static final int EC_FRIEND_ALREADY_EXIST = 2103;
    public static final int EC_FRIEND_NOT_EXIST = 2104;
    public static final int EC_SELF_FRIEND_COUNT_MAX = 2105;
    public static final int EC_OTHER_FRIEND_COUNT_MAX = 2106;

    public FriendException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static FriendException playerNotExist() {
        return new FriendException(EC_PLAYER_NOT_EXIST, "角色不存在");
    }

    public static FriendException applyAlreadyExist() {
        return new FriendException(EC_APPLY_ALREADY_EXIST, "好友申请已经存在");
    }

    public static FriendException applyNotExist() {
        return new FriendException(EC_APPLY_NOT_EXIST, "好友申请不存在");
    }

    public static FriendException friendAlreadyExist() {
        return new FriendException(EC_FRIEND_ALREADY_EXIST, "好友已经存在");
    }

    public static FriendException friendNotExist() {
        return new FriendException(EC_FRIEND_NOT_EXIST, "好友不存在");
    }

    public static FriendException selfFriendCountMax() {
        return new FriendException(EC_SELF_FRIEND_COUNT_MAX, "自己的好友数量已达上限");
    }

    public static FriendException otherFriendCountMax() {
        return new FriendException(EC_OTHER_FRIEND_COUNT_MAX, "对方的好友数量已达上限");
    }

}
