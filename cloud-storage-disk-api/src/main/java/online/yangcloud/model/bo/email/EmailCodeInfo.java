package online.yangcloud.model.bo.email;

import java.util.Date;

/**
 * @author zhuboyang
 * @since 2022年03月05 22:38:59
 */
public class EmailCodeInfo {

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮箱验证码
     */
    private String emailCode;

    /**
     * 生效时间
     */
    private Date startTime;

    /**
     * 过期时间
     */
    private Integer expireTime;

    public String getEmail() {
        return email;
    }

    public EmailCodeInfo setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public EmailCodeInfo setEmailCode(String emailCode) {
        this.emailCode = emailCode;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public EmailCodeInfo setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Integer getExpireTime() {
        return expireTime;
    }

    public EmailCodeInfo setExpireTime(Integer expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    @Override
    public String toString() {
        return "EmailCodeInfo["
                + " email=" + email + ","
                + " emailCode=" + emailCode + ","
                + " startTime=" + startTime + ","
                + " expireTime=" + expireTime
                + " ]";
    }
}
