package online.yangcloud.common.common;

import online.yangcloud.common.common.resultcode.AppResultCode;

/**
 * @author zhuboyang
 * @since 2021/9/8 07:50
 */
public class ResultCode {

    /**
     * code码
     */
    private final Integer code;
    /**
     * 描述
     */
    private final String message;

    private ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * new
     *
     * @param code    code 码
     * @param message 描述
     * @return ResultCode
     */
    public static ResultCode newInstance(Integer code, String message) {
        return new ResultCode(code, message);
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 方法描述：根据 code clone，
     *
     * @param message 描述
     * @return ResultCode
     */
    public ResultCode clone(String message) {
        return new ResultCode(this.code, message);
    }

    /**
     * 对比是否是同一个code
     *
     * @param code code 码
     * @return 是否
     */
    boolean is(Integer code) {
        return getCode().equals(code);
    }

    /**
     * 对比是否是同一个code
     *
     * @param code code 码
     * @return 是否
     */
    boolean is(String code) {
        return getCode().toString().equals(code);
    }

    /**
     * 是否为成功的返回码
     *
     * @return boolean
     */
    public boolean isSuccess() {
        return this.is(AppResultCode.SUCCESS.code);
    }

    @Override
    public String toString() {
        return "ResultCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
