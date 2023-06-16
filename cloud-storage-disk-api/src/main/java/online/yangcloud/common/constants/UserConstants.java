package online.yangcloud.common.constants;

/**
 * @author zhuboyang
 * @since 2022年03月12 22:19:42
 */
public interface UserConstants {

    /**
     * 输入密码错误的锁
     */
    String ERROR_PASSWORD = "error_password:";

    /**
     * 输入错误密码锁定的次数
     */
    int ERROR_PASSWORD_LOCK_COUNT = 5;

    /**
     * 密码输入错误后，账户锁定的时间（分）
     */
    int ERROR_PASSWORD_LOCK_TIME = 5;

}
