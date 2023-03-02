package online.yangcloud.exception;

/**
 * 参数异常
 *
 * @author zhuby
 * @since 2021/4/12 08:20
 */

public class ParamErrorException extends RuntimeException {

    public ParamErrorException() {
    }

    public ParamErrorException(String message) {
        super(message);
    }

    public ParamErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamErrorException(Throwable cause) {
        super(cause);
    }

    public ParamErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
