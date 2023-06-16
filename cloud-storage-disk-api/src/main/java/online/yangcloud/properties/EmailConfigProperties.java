package online.yangcloud.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhuboyang
 * @since 2023/3/7 21:16
 */
@Component
@ConfigurationProperties(prefix = "email")
public class EmailConfigProperties {

    /**
     * Account of the email sender
     */
    private String sendFrom;

    /**
     * Mail Server
     */
    private String sendHost;

    /**
     * Mail Server Port
     */
    private Integer emailPort;

    /**
     * Mail Server Password (not account password)
     */
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
