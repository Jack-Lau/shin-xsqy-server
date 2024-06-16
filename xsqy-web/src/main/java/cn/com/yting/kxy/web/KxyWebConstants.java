/*
 * Created 2018-10-12 19:55:19
 */
package cn.com.yting.kxy.web;

import java.util.regex.Pattern;

/**
 *
 * @author Azige
 */
public final class KxyWebConstants {

    public static final Pattern PLAYER_NAME_PATTERN = Pattern.compile("[\u4E00-\u9FA5]{2,6}");

    public static final int ACCOUNT_ID_NO_OWNER = 0;

    private KxyWebConstants() {
    }

}
