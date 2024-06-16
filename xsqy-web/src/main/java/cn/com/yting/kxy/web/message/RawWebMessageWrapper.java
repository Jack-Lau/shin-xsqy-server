/*
 * Created 2018-10-27 16:13:23
 */
package cn.com.yting.kxy.web.message;

import lombok.Value;

/**
 * 包装一个需要以原始格式输出的消息
 *
 * @author Azige
 */
@Value
public class RawWebMessageWrapper {

    private Object message;
}
