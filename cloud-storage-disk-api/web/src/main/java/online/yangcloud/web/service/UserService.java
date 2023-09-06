package online.yangcloud.web.service;

import online.yangcloud.common.model.User;
import online.yangcloud.common.model.request.user.PasswordUpdater;
import online.yangcloud.common.model.request.user.UserEnter;
import online.yangcloud.common.model.request.user.UserInitializer;
import online.yangcloud.common.model.request.user.UserUpdater;
import online.yangcloud.common.model.view.user.UserView;

import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年01月18 11:19:23
 */
public interface UserService {

    /**
     * 初始化账户信息
     *
     * @param initializer 账户邮箱和密码（加密过的）
     */
    void initialize(UserInitializer initializer);

    /**
     * 用户登录
     *
     * @param enter 邮箱地址与密码
     * @return 会话 session id
     */
    String enter(UserEnter enter);

    /**
     * 修改账户资料
     *
     * @param updater 要修改的资料
     * @param user    当前登录的账户
     * @return 修改后的账户资料
     */
    UserView updateUserInfo(UserUpdater updater, User user);

    /**
     * 修改账户密码
     *
     * @param updater 新密码
     * @param user    当前登录的用户
     * @return 修改后的账户资料
     */
    UserView updatePassword(PasswordUpdater updater, User user);

}
