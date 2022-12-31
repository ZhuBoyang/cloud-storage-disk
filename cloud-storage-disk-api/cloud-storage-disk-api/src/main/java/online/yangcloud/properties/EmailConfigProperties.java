package online.yangcloud.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhuboyang
 * @since 2021-12-2021/12/10 11:20:16
 */
@Component
@ConfigurationProperties(prefix = "email")
public class EmailConfigProperties {

    private String sendFrom;

    private String sendHost;

    private Integer emailPort;

    private String emailPass;

    public String getSendFrom() {
        return sendFrom;
    }

    public void setSendFrom(String sendFrom) {
        this.sendFrom = sendFrom;
    }

    public String getSendHost() {
        return sendHost;
    }

    public void setSendHost(String sendHost) {
        this.sendHost = sendHost;
    }

    public Integer getEmailPort() {
        return emailPort;
    }

    public void setEmailPort(Integer emailPort) {
        this.emailPort = emailPort;
    }

    public String getEmailPass() {
        return emailPass;
    }

    public void setEmailPass(String emailPass) {
        this.emailPass = emailPass;
    }

    @Override
    public String toString() {
        return "EmailConfigProperties["
                + " sendFrom=" + sendFrom + ","
                + " sendHost=" + sendHost + ","
                + " emailPort=" + emailPort + ","
                + " emailPass=" + emailPass
                + " ]";
    }
}
