package online.yangcloud.common.resultcode;

import online.yangcloud.common.ResultCode;

/**
 * @author zhuboyang
 * @since 2022年03月12 11:36:01
 */
public interface UserResultCode {

    interface UserMsg {
        /**
         * 您还未登录，无法进行此操作，请先进行登录
         */
        String NO_AUTH_SESSION = "您还未登录，无法进行此操作，请先进行登录";
        /**
         * 用户添加失败
         */
        String USER_SAVE_ERROR = "用户添加失败";
        /**
         * 注册失败，请重试。
         */
        String REGISTER_ERROR = "注册失败，请重试";
        /**
         * 注册失败，请重试。
         */
        String REGISTER_SUCCESS = "注册成功，请前往登录，页面跳转中……";
        /**
         * 此账户不存在
         */
        String ACCOUNT_NOT_EXIST = "此账户不存在";
        /**
         * 此账户已存在，请前往登录
         */
        String ACCOUNT_HAS_EXIST = "此账户已存在，请前往登录";
        /**
         * 此账户已存在，请勿重复添加
         */
        String ACCOUNT_HAD_EXIST = "此账户已存在，请勿重复添加";
        /**
         * 邮箱或密码输入错误，请重试
         */
        String ACCOUNT_OR_PASSWORD_ERROR = "邮箱或密码输入错误，请重试";
        /**
         * 账户已锁定，五分钟后解锁
         */
        String ACCOUNT_HAS_BEEN_LOCKED = "账户已锁定，5分钟后解锁";
        /**
         * 登录成功，正在进行跳转
         */
        String LOGIN_SUCCESS_AND_REDIRECT = "登录成功，正在进行跳转";
        /**
         * 设置失败，请重试
         */
        String SETTING_ERROR = "设备失败，请重试";
        /**
         * 设置成功
         */
        String SETTING_SUCCESS = "设置成功";
        /**
         * 账户已被删除
         */
        String ACCOUNT_HAS_BEEN_DELETE = "账户已被删除";
        /**
         * 您的账户还未激活，尚无法登录
         */
        String ACCOUNT_NOT_ACTIVITY = "您的账户还未激活，尚无法登录";
        /**
         * 您的账户还未激活，无法修改密码
         */
        String ACCOUNT_HAD_NOT_ACTIVITY = "您的账户还未激活，无法修改密码";
        /**
         * 密码修改失败
         */
        String PASSWORD_UPDATE_ERROR = "密码修改失败";
        /**
         * 密码修改成功
         */
        String PASSWORD_UPDATE_SUCCESS = "密码修改成功，请前往登录，正在跳转……";
    }

    /**
     * 会话已失效
     */
    ResultCode NO_AUTH_SESSION = ResultCode.newInstance(11000, UserMsg.NO_AUTH_SESSION);

}
