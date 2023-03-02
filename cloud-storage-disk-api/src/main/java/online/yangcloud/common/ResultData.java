package online.yangcloud.common;

/**
 * 统一http返回值
 *
 * @author zhuby
 * @since 2020/3/23 22:04
 */

public class ResultData {

    private static final int SUCCESS_CODE = 200;
    private static final int ERROR_MESSAGE = 500;
    private static final int ERROR_MAP = 501;
    private static final int ERROR_TOKEN_MESSAGE = 502;
    private static final int ERROR_EXCEPTION = 555;
    private static final int ERROR_USER_QQ = 556;
    private static final String ERROR_STR = "error";
    private static final String EMPTY_STR = "";

    /**
     * 响应业务状态
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应中的数据
     */
    private Object data;

    public static ResultData build(Integer code, String message, Object data) {
        return new ResultData(code, message, data);
    }

    public static ResultData build(ResultCode code, Object data) {
        return new ResultData(code.getCode(), code.getMessage(), data);
    }

    public static ResultData build(ResultCode code) {
        return new ResultData(code.getCode(), code.getMessage(), EMPTY_STR);
    }

    public static ResultData success(ResultCode code, Object data) {
        return new ResultData(SUCCESS_CODE, code.getMessage(), data);
    }

    public static ResultData success(Object data) {
        return new ResultData(SUCCESS_CODE, EMPTY_STR, data);
    }

    public static ResultData success(ResultCode code) {
        return new ResultData(SUCCESS_CODE, code.getMessage(), EMPTY_STR);
    }

    public static ResultData success() {
        return new ResultData(EMPTY_STR);
    }

    public static ResultData errorMessage() {
        return new ResultData(ERROR_MESSAGE, EMPTY_STR, EMPTY_STR);
    }

    public static ResultData errorMessage(ResultCode code) {
        return new ResultData(code.getCode(), code.getMessage(), EMPTY_STR);
    }

    public static ResultData errorMessage(ResultCode code, Object data) {
        return new ResultData(ERROR_MESSAGE, code.getMessage(), data);
    }

    public static ResultData errorMap(Object data) {
        return new ResultData(ERROR_MAP, ERROR_STR, data);
    }

    public static ResultData errorTokenMsg(ResultCode code) {
        return new ResultData(ERROR_TOKEN_MESSAGE, code.getMessage(), EMPTY_STR);
    }

    public static ResultData errorException(ResultCode code) {
        return new ResultData(ERROR_EXCEPTION, code.getMessage(), EMPTY_STR);
    }

    public static ResultData errorUserQq(ResultCode code) {
        return new ResultData(ERROR_USER_QQ, code.getMessage(), EMPTY_STR);
    }

    public ResultData() {

    }

    public ResultData(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResultData(Object data) {
        this.code = SUCCESS_CODE;
        this.message = EMPTY_STR;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultData{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

}
