/*
 * Created 2018-7-19 17:16:28
 */
package cn.com.yting.kxy.web.chat;

/**
 *
 * @author Azige
 */
public final class ChatConstants {

    public static final long SERVICE_ID_UNDIFINED = -1;
    public static final long SERVICE_ID_GAME_MASTER = 0;

    public static final int PRIVATE_MESSAGE_MAX_COUNT = 50;

    public static final long INTERVAL_LIMIT_PUBLIC_MESSAGE = 20_000;
    public static final long INTERVAL_LIMIT_PRIVATE_MESSAGE = 1_000;

    private ChatConstants() {
    }
}
