package online.yangcloud.service.impl;

import cn.hutool.json.JSONUtil;
import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.model.ao.email.EmailSender;
import online.yangcloud.model.bo.email.EmailCodeInfo;
import online.yangcloud.service.EmailService;
import online.yangcloud.utils.EmailTools;
import online.yangcloud.utils.ExceptionTools;
import online.yangcloud.utils.RedisTools;
import online.yangcloud.utils.ValidateTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuboyang
 * @since 2023年06月12 16:06:29
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EmailServiceImpl implements EmailService {

    @Resource
    private EmailTools emailTools;

    @Resource
    private RedisTools redisTools;

    @Override
    public void sendRegisterEmail(EmailSender sender) {
        // 校验邮箱地址是否合法
        if (!ValidateTools.validEmail(sender.getEmail())) {
            ExceptionTools.paramLogger();
        }

        // 封装验证码邮件信息
        EmailCodeInfo emailCodeInfo = emailTools.generateEmailCode(sender.getEmail());

        // 生成邮件正文信息
        String emailContent = emailTools.generateEmailContent(emailCodeInfo, "templates/regEmailValidation.html");

        // 发送邮件
//        MailUtil.send(sender.getEmail(), "平台注册校验码", emailContent, Boolean.TRUE);

        // 将邮箱地址记入 redis，确保在 5 分钟内无法再次发送验证码
        Boolean setResult = redisTools.setIfAbsent(AppConstants.Email.REGISTER_EMAIL_REDIS_KEY + sender.getEmail(),
                JSONUtil.toJsonStr(emailCodeInfo),
                AppConstants.Email.EMAIL_CODE_EXPIRED_TIME,
                TimeUnit.MINUTES);
        if (setResult) {
            System.out.println("验证码已发送 ==========> " + emailCodeInfo);
        } else {
            ExceptionTools.businessLogger("验证码已发送，请勿重复发送");
        }
    }

}
