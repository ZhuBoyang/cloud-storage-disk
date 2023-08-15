package online.yangcloud.common.common;

import online.yangcloud.common.common.resultcode.AppResultCode;

/**
 * @author zhuboyang
 * @since 2022/12/31 11:43
 */
public class ResultBean<T> {

    /**
     * 状态
     */
    private ResultCode resultCode;

    /**
     * 数据
     */
    private T bean;

    private Exception e;

    public ResultBean() {
    }

    public ResultBean(ResultCode resultCode, T bean) {
        this.resultCode = resultCode;
        this.bean = bean;
    }

    public static <T> ResultBean<T> resultCode(ResultCode resultCode) {
        return new ResultBean<>(resultCode, null);
    }

    public static <T> ResultBean<T> resultCode(ResultCode resultCode, T bean) {
        return new ResultBean<>(resultCode, bean);
    }

    public static <T> ResultBean<T> error() {
        return new ResultBean<>(AppResultCode.FAILURE, null);
    }

    public static <T> ResultBean<T> error(T bean) {
        return new ResultBean<>(AppResultCode.FAILURE, bean);
    }

    public static <T> ResultBean<T> success() {
        return new ResultBean<>(AppResultCode.SUCCESS, null);
    }

    public static <T> ResultBean<T> success(T bean) {
        return new ResultBean<>(AppResultCode.SUCCESS, bean);
    }


    public ResultBean<T> bean(T bean) {
        this.setBean(bean);
        return this;
    }

    public boolean isSuccess() {
        return null != resultCode && AppResultCode.SUCCESS.getCode().equals(resultCode.getCode());
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public T getBean() {
        return bean;
    }

    public void setBean(T bean) {
        this.bean = bean;
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "resultCode=" + resultCode +
                ", bean=" + bean +
                '}';
    }
}
