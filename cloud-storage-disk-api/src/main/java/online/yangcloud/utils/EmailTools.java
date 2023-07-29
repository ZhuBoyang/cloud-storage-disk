package online.yangcloud.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.model.bo.email.EmailCodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author zhuboyang
 * @since 2022年03月12 15:06:21
 */
public class EmailTools {

    private static final Logger logger = LoggerFactory.getLogger(EmailTools.class);

    private static final MailAccount ACCOUNT = new MailAccount();

    static {
        try {
            File mailSetting = FileUtil.file(SystemTools.systemPath() + "mail_config.txt");
            String mailContents = IoUtil.read(Files.newInputStream(mailSetting.toPath()), StandardCharsets.UTF_8);
            List<String> mailConfigs = StrUtil.split(mailContents, StrUtil.LF);
            for (String o : mailConfigs) {
                if (StrUtil.isBlank(o) || o.startsWith(AppConstants.Special.POUND)) {
                    continue;
                }
                List<String> arr = StrUtil.split(o, AppConstants.Special.EQUAL);
                if ("host".equals(StrUtil.trim(arr.get(0)))) {
                    ACCOUNT.setHost(StrUtil.trim(arr.get(1)));
                }
                if ("port".equals(StrUtil.trim(arr.get(0)))) {
                    ACCOUNT.setPort(Integer.parseInt(StrUtil.trim(arr.get(1))));
                }
                if ("from".equals(StrUtil.trim(arr.get(0)))) {
                    ACCOUNT.setFrom(StrUtil.trim(arr.get(1)));
                }
                if ("user".equals(StrUtil.trim(arr.get(0)))) {
                    ACCOUNT.setUser(StrUtil.trim(arr.get(1)));
                }
                if ("pass".equals(StrUtil.trim(arr.get(0)))) {
                    ACCOUNT.setPass(StrUtil.trim(arr.get(1)));
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 获取邮箱配置
     *
     * @return 邮箱配置
     */
    public static MailAccount account() {
        return ACCOUNT;
    }

    /**
     * 生成邮箱验证码信息
     *
     * @param email 邮箱
     * @return 邮箱验证码信息
     */
    public static EmailCodeInfo generateEmailCode(String email) {
        String emailCode = String.valueOf(new Random().nextInt(900000) + 100000);
        EmailCodeInfo emailCodeInfo = new EmailCodeInfo()
                .setEmail(email)
                .setEmailCode(emailCode)
                .setStartTime(DateUtil.date())
                .setExpireTime(AppConstants.Email.EMAIL_CODE_EXPIRED_TIME);
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
    public static String generateEmailContent(EmailCodeInfo emailCodeInfo, String templateHtmlUrl) {
        String emailHtml = ResourceUtil.readUtf8Str(templateHtmlUrl);
        if (ObjectUtil.isNotNull(emailCodeInfo) && CharSequenceUtil.isNotBlank(emailCodeInfo.getEmailCode())) {
            emailHtml = emailHtml.replace("#email.code", emailCodeInfo.getEmailCode());
        }
        emailHtml = emailHtml.replace("#expire.time", String.valueOf(AppConstants.Email.EMAIL_CODE_EXPIRED_TIME));
        emailHtml = emailHtml.replace("#send.from", ACCOUNT.getFrom());
        emailHtml = emailHtml.replace("#send.time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return emailHtml;
    }

}
