package online.yangcloud.common.constants;

/**
 * @author zhuboyang
 * @since 2022年03月12 22:19:42
 */
public interface UserConstants {

    /**
     * 会话标识
     */
    String AUTHORIZATION = "s";

    /**
     * 用户登录会话前缀
     */
    String LOGIN_SESSION = "user_login_session:";

    /**
     * 账号登录session会话过期时间：7 * 24小时
     */
    Integer LOGIN_SESSION_EXPIRE_TIME = 7 * 24 * 60 * 60;

    /**
     * 初始化的邮箱
     */
    String INIT_EMAIL = "zhuboyang1996@foxmail.com";

    /**
     * 默认密码
     */
    String DEFAULT_PASSWORD = "Jhxz951129Jhxz";

    /**
     * 输入密码错误的锁
     */
    String ERROR_PASSWORD = "error_password:";

    /**
     * 输入错误密码锁定的次数
     */
    int ERROR_PASSWORD_LOCK_COUNT = 5;

    /**
     * 密码输入错误后，账户锁定的时间（秒）
     */
    int ERROR_PASSWORD_LOCK_TIME = 5;

}
