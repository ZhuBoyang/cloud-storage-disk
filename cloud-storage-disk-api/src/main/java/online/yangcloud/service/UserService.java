package online.yangcloud.service;

import online.yangcloud.model.ao.user.UserEnter;
import online.yangcloud.model.ao.user.UserRegister;

/**
 * @author zhuboyang
 * @since 2023年01月18 11:19:23
 */
public interface UserService {

    /**
     * 注册账户
     *
     * @param register 账户邮箱和密码（加密过的）
     */
    void register(UserRegister register);

    /**
     * 用户登录
     *
     * @param enter 邮箱地址与密码
     * @return 会话 session id
     */
    String enter(UserEnter enter);

}
