package online.yangcloud.common.constants;

/**
 * @author zhuboyang
 * @since 2022年03月12 22:19:42
 */
public interface UserConstants {

    /**
     * 会话标识
     */
    String AUTHORIZATION = "authorization";

    /**
     * 用户登录会话前缀
     */
    String LOGIN_SESSION = "user_login_session:";

    /**
     * 输入密码错误的锁
     */
    String ERROR_PASSWORD = "error_password:";

    /**
     * 账号登录 session 会话过期时间：7 * 24 小时
     */
    Integer LOGIN_SESSION_EXPIRE_TIME = 7 * 24 * 60 * 60;

    /**
     * 输入错误密码锁定的次数
     */
    int ERROR_PASSWORD_LOCK_COUNT = 5;

    /**
     * 密码输入错误后，账户锁定的时间（分）
     */
    int ERROR_PASSWORD_LOCK_TIME = 5;

}
