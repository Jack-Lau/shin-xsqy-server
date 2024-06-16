/*
 * Created 2018-10-31 17:12:13
 */
package cn.com.yting.kxy.web.player;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class PlayerLevelAndExp {

    private long accountId;
    private int level;
    private long exp;
}
