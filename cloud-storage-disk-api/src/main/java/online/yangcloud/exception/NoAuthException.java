package online.yangcloud.exception;

import online.yangcloud.common.ResultCode;
import online.yangcloud.common.resultcode.UserResultCode;
import online.yangcloud.enumration.SuperEnum;

/**
 * 权限不足异常
 *
 * @author zhuby
 * @since 2021/4/12 08:20
 */

public class NoAuthException extends RuntimeException implements SuperEnum<Integer> {

    /**
     * code 码
     */
    private Integer code;

    /**
     * 说明描述
     */
    private String msg;

    public NoAuthException() {
        super();
        code = UserResultCode.NO_AUTH_SESSION.getCode();
        msg = UserResultCode.NO_AUTH_SESSION.getMessage();
    }

    public NoAuthException(ResultCode resultCode) {
        super();
        code = resultCode.getCode();
        msg = resultCode.getMessage();
    }

    public NoAuthException(String message) {
        super(message);
    }

    public NoAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAuthException(Throwable cause) {
        super(cause);
    }

    public NoAuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getValue() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "NoAuthException["
                + " code=" + code + ","
                + " msg=" + msg
                + " ]"
                + " "
                + super.toString();
    }
}
