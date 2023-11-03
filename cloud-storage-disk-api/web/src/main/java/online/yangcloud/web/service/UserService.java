package online.yangcloud.web.service;

import online.yangcloud.common.model.User;
import online.yangcloud.common.model.request.user.PasswordUpdater;
import online.yangcloud.common.model.request.user.UserEnter;
import online.yangcloud.common.model.request.user.UserInitializer;
import online.yangcloud.common.model.request.user.UserUpdater;
import online.yangcloud.common.model.view.user.UserView;

import java.io.IOException;

/**
 * @author zhuboyang
 * @since 2023年01月18 11:19:23
 */
public interface UserService {

    /**
     * 系统是否进行了初始化
     *
     * @return 结果
     */
    boolean hadInitialized();

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
    String enter(UserEnter enter) throws IOException;

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

    /**
     * 更新用户账户的空间增量
     *
     * @param sessionId 会话 id
     * @param user      要更新的用户
     */
    void updateIncrementSize(String sessionId, User user);

    /**
     * 跟新 redis 缓存
     *
     * @param user       用户账户信息
     * @param expireTime 过期时间/秒
     */
    void updateRedisCache(User user, Integer expireTime);

    /**
     * 跟新 redis 缓存
     *
     * @param view       用户账户信息
     * @param expireTime 过期时间/秒
     */
    void updateRedisCache(UserView view, Integer expireTime);

    /**
     * 跟新 redis 缓存
     *
     * @param sessionId  会话 id
     * @param user       用户账户信息
     * @param expireTime 过期时间/秒
     */
    void updateRedisCache(String sessionId, User user, Integer expireTime);

    /**
     * 跟新 redis 缓存
     *
     * @param sessionId  会话 id
     * @param view       用户账户信息
     * @param expireTime 过期时间/秒
     */
    void updateRedisCache(String sessionId, UserView view, Integer expireTime);

}
