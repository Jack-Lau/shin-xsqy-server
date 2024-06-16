/*
 * Created 2018-7-6 10:53:06
 */
package cn.com.yting.kxy.web.player;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Azige
 */
public class PlayerException extends KxyWebException {

    public static final int EC_PLAYER_ALREADY_CREATED = 200;
    public static final int EC_PLAYERNAME_EXISTED = 201;
    public static final int EC_PLAYERNAME_ILLEGAL = 202;
    public static final int EC_PREFABID_ILLEGAL = 203;
    public static final int EC_改名卡不足 = 204;
    public static final int EC_新名称是他人的曾用名 = 205;

    public PlayerException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static PlayerException playerAlreadyCreated() {
        return new PlayerException(EC_PLAYER_ALREADY_CREATED, "此账号已经创建过角色");
    }

    public static PlayerException playerNameExisted(String name) {
        return new PlayerException(EC_PLAYERNAME_EXISTED, "角色名已存在：" + name);
    }

    public static PlayerException playerNameIllegal(String name) {
        return new PlayerException(EC_PLAYERNAME_ILLEGAL, "角色名非法：" + name);
    }

    public static PlayerException prefabIdIllegal(long id) {
        return new PlayerException(EC_PREFABID_ILLEGAL, "造型id非法：" + id);
    }

    public static PlayerException 改名卡不足() {
        return new PlayerException(EC_改名卡不足, "改名卡不足");
    }

    public static PlayerException 新名称是他人的曾用名() {
        return new PlayerException(EC_新名称是他人的曾用名, "新名称是他人的曾用名");
    }

}
