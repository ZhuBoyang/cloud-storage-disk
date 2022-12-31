package online.yangcloud.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import online.yangcloud.common.constants.EmailConstants;
import online.yangcloud.model.bo.EmailCodeInfo;
import online.yangcloud.properties.EmailConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author zhuboyang
 * @since 2022年03月12 15:06:21
 */
@Component
public class EmailUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EmailConfigProperties emailConfigProperties;

    /**
     * 生成邮箱验证码信息
     *
     * @param email 邮箱
     * @return 邮箱验证码信息
     */
    public EmailCodeInfo generateEmailCode(String email) {
        String emailCode = String.valueOf(new Random().nextInt(900000) + 100000);
        EmailCodeInfo emailCodeInfo = new EmailCodeInfo()
                .setEmail(email)
                .setEmailCode(emailCode)
                .setStartTime(System.currentTimeMillis())
                .setExpireTime(EmailConstants.EMAIL_CODE_EXPIRED_TIME);
        logger.info("\n注册邮箱：\t{}\n邮箱验证码：\t{}\n生效时间：\t{}\n过期时间：\t{}",
                emailCodeInfo.getEmail(), emailCodeInfo.getEmailCode(), emailCodeInfo.getStartTime(), emailCodeInfo.getExpireTime());
        return emailCodeInfo;
    }

    /**
     * 生成邮件内容
     *
     * @param emailCodeInfo   验证码信息
     * @param templateHtmlUrl 邮件模板路径
     * @return result
     */
    public String generateEmailContent(EmailCodeInfo emailCodeInfo, String templateHtmlUrl) {
        String emailHtml = ResourceUtil.readUtf8Str(templateHtmlUrl);
        emailHtml = emailHtml.replace("#email.code", emailCodeInfo.getEmailCode());
        emailHtml = emailHtml.replace("#expire.time", EmailConstants.EMAIL_CODE_EXPIRE_TIME);
        emailHtml = emailHtml.replace("#send.from", emailConfigProperties.getSendFrom());
        emailHtml = emailHtml.replace("#send.time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return emailHtml;
    }

}
