package online.yangcloud.service;

import online.yangcloud.model.ao.email.EmailSender;

/**
 * @author zhuboyang
 * @since 2023年06月12 16:06:19
 */
public interface EmailService {

    /**
     * 发送账户注册的邮件
     *
     * @param sender 邮箱地址
     */
    void sendRegisterEmail(EmailSender sender);

}
