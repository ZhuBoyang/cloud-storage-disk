package online.yangcloud.common.constants;

/**
 * @author zhuboyang
 * @since 2022年03月12 16:26:02
 */
public interface EmailConstants {

    /**
     * 邮箱验证码过期时间（时间单位：m）
     */
    String EMAIL_CODE_EXPIRE_TIME = "5";

    /**
     * 邮箱验证码过期时间（时间单位：ms）
     */
    Integer EMAIL_CODE_EXPIRED_TIME = 5 * 60 * AppConstants.TIME_CONVERSION_UNIT.intValue();

    /**
     * 用于注册的邮箱验证码的 redis key
     */
    String REGISTER_EMAIL_REDIS_KEY = "register_email_code:";

    /**
     * 用于重置密码的邮箱验证码的 redis key
     */
    String RESET_PASSWORD_REDIS_KEY = "reset_password:";

    /**
     * 邮箱正则
     */
    String EMAIL_REGULAR = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

}
