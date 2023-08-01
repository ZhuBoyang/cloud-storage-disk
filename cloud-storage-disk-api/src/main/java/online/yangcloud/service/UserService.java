package online.yangcloud.service;

import online.yangcloud.model.User;
import online.yangcloud.model.request.user.UserEnter;
import online.yangcloud.model.request.user.UserRegister;

import java.util.List;

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

    /**
     * 更新用户账户空间
     *
     * @param keys 失效 redis key
     * @param user 更新的账户
     */
    void updateUserSpace(List<String> keys, User user);

}
