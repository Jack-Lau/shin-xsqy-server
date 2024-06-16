/*
 * Created 2018-6-25 15:52:53
 */
package cn.com.yting.kxy.web;

/**
 * 定义应用程序异常的基础类型，以及一些通用的错误码
 *
 * @author Azige
 */
public class KxyWebException extends RuntimeException {

    /**
     * 用于未定义的错误类型，或是暂时不需要标记错误类型的异常
     */
    public static final int EC_UNKNOW = -1;

    /**
     * 用于服务端内部问题引起的异常
     */
    public static final int EC_INTERNAL_ERROR = -2;

    /**
     * 用于表示一个查找的资源不存在
     */
    public static final int EC_NOT_FOUND = 1;

    /**
     * 因为数据库死锁等原因导致服务暂时不可用
     */
    public static final int EC_CONCURRENCY_ERROR = 2;

    private final int errorCode;

    public KxyWebException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public KxyWebException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public static KxyWebException unknown(String message) {
        return new KxyWebException(EC_UNKNOW, message);
    }

    public static KxyWebException unknown(String message, Throwable cause) {
        return new KxyWebException(EC_UNKNOW, message, cause);
    }

    public static KxyWebException internalError(String message) {
        return new KxyWebException(EC_INTERNAL_ERROR, message);
    }

    public static KxyWebException internalError(String message, Throwable cause) {
        return new KxyWebException(EC_INTERNAL_ERROR, message, cause);
    }

    public static NotFoundException notFound(String message) {
        return new NotFoundException(message);
    }

    public static class NotFoundException extends KxyWebException {

        public NotFoundException(String message) {
            super(EC_NOT_FOUND, message);
        }
    }
}
