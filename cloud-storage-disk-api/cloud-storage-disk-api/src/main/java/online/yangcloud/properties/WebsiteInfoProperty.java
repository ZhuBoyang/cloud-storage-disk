package online.yangcloud.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhuboyang
 * @since 2022年07月05 10:29:18
 */
@Component
@ConfigurationProperties(prefix = "website.info")
public class WebsiteInfoProperty {

    /**
     * 网站创建时间
     */
    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "WebsiteInfoProperty["
                + " createTime=" + createTime
                + " ]";
    }
}
