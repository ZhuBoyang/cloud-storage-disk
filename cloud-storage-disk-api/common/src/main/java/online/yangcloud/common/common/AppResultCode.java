package online.yangcloud.common.common;

/**
 * @author zhuboyang
 * @since 2023/8/14 22:11
 */
public interface AppResultCode {

    interface Msg {
        /**
         * 成功
         */
        String SUCCESS = "操作成功";
        /**
         * 很抱歉，您的操作失败了，建议您重试一下!。
         */
        String UNKNOWN_ERROR = "很抱歉，您的操作失败了，建议您重试一下!";
        /**
         * 参数错误
         */
        String INVALID_PARAM_VALUE = "参数错误";
        /**
         * 请求 method 错误
         */
        String HTTP_REQUEST_METHOD_NOT_SUPPORTED = "请求方法错误";
        /**
         * 数据删除失败
         */
        String DB_DATA_DELETE_FAILURE = "数据删除失败";
        /**
         * 数据未找到
         */
        String DATA_NOT_FOUND = "数据未找到";
        /**
         * 网盘空间不足，无法完成操作！请增加磁盘存储。
         */
        String DISK_SIZE_NOT_ENOUGH = "磁盘空间不足";
    }

    /**
     * 成功
     */
    ResultCode SUCCESS = ResultCode.newInstance(200, Msg.SUCCESS);
    /**
     * 失败
     * unknown\ error=很抱歉，您的操作失败了，我们建议您重试一下!重试后，如仍然无法解决，请联系管理员或网盘客服。
     */
    ResultCode FAILURE = ResultCode.newInstance(10000, Msg.UNKNOWN_ERROR);
    /**
     * 数据未找到
     */
    ResultCode DATA_NOT_FOUND = ResultCode.newInstance(10001, Msg.DATA_NOT_FOUND);
    /**
     * 数据删除失败
     */
    ResultCode DB_DATA_DELETE_FAILURE = ResultCode.newInstance(10002, Msg.DB_DATA_DELETE_FAILURE);
    /**
     * 磁盘空间不足
     */
    ResultCode DISK_SIZE_NOT_ENOUGH = ResultCode.newInstance(10003, Msg.DISK_SIZE_NOT_ENOUGH);

    /* - 参数类错误码 - */
    /**
     * 参数错误 返回"参数错误"
     */
    ResultCode INVALID_PARAM_VALUE = ResultCode.newInstance(10004, Msg.INVALID_PARAM_VALUE);
    /**
     * 请求 method 错误
     */
    ResultCode HTTP_REQUEST_METHOD_NOT_SUPPORTED = ResultCode.newInstance(10005, Msg.HTTP_REQUEST_METHOD_NOT_SUPPORTED);
}
