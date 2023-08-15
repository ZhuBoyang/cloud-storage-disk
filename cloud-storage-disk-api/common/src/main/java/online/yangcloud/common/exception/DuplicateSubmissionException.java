package online.yangcloud.common.exception;

/**
 * 业务异常
 *
 * @author zhuboyang
 * @since 2023/3/7 21:15
 */
public class DuplicateSubmissionException extends RuntimeException {

    public DuplicateSubmissionException() {
    }

    public DuplicateSubmissionException(String message) {
        super(message);
    }

    public DuplicateSubmissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateSubmissionException(Throwable cause) {
        super(cause);
    }

    public DuplicateSubmissionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
