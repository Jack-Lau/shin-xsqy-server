/*
 * Created 2019-2-19 18:27:27
 */
package cn.com.yting.kxy.web.topone;

/**
 *
 * @author Azige
 */
public class TopOneApiException extends RuntimeException {

    public TopOneApiException(String message) {
        super(message);
    }

    public TopOneApiException(Throwable cause) {
        super(cause);
    }

}
