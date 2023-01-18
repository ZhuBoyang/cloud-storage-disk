package online.yangcloud.service;

import online.yangcloud.common.ResultBean;
import online.yangcloud.model.vo.user.UserView;

/**
 * @author zhuboyang
 * @since 2023年01月18 11:19:23
 */
public interface UserService {

    /**
     * 添加用户
     *
     * @param userName 用户名
     * @param email    邮箱
     * @param password 密码
     * @return result
     */
    ResultBean<?> addUser(String userName, String email, String password);

    /**
     * 登录
     *
     * @param email    账户邮箱
     * @param password 账户密码
     * @return result
     */
    ResultBean<UserView> login(String email, String password);

}
