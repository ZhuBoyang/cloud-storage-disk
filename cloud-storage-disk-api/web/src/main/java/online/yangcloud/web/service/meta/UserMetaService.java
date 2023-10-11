package online.yangcloud.web.service.meta;

import online.yangcloud.common.model.User;

/**
 * @author zhuboyang
 * @since 2023年06月12 15:28:12
 */
public interface UserMetaService {

    /**
     * 记录用户账户信息
     *
     * @param user 用户账户信息
     */
    void insertUser(User user);

    /**
     * 更新账户空间已用量
     *
     * @param user      当前操作用户
     * @param spaceSize 本次修改的使用量
     */
    void updateSpaceSize(User user, Long spaceSize);

    /**
     * 更新账户空间已用量
     *
     * @param user 当前操作用户
     */
    void updateSpaceSize(User user);

    /**
     * 更新用户账户信息
     *
     * @param user 新的账户信息
     */
    void updateUser(User user);

    /**
     * 查询用户信息
     *
     * @param id 用户 id
     * @return 用户信息
     */
    User queryUserById(String id);

    /**
     * 查询用户信息
     *
     * @param email 用户邮箱
     * @return 用户信息
     */
    User queryUserByEmail(String email);

}
