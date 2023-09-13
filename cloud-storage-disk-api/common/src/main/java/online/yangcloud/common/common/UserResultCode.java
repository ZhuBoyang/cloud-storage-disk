package online.yangcloud.common.common;

/**
 * @author zhuboyang
 * @since 2022年03月12 11:36:01
 */
public interface UserResultCode {

    interface UserMsg {
        /**
         * 您还未登录，无法进行此操作，请先进行登录
         */
        String NO_AUTH_SESSION = "您还未登录，无法进行此操作，请先进行登录";
    }

    /**
     * 会话已失效
     */
    ResultCode NO_AUTH_SESSION = ResultCode.newInstance(11000, UserMsg.NO_AUTH_SESSION);

}
