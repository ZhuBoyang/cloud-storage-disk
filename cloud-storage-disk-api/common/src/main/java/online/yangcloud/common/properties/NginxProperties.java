package online.yangcloud.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhuboyang
 * @since 2023年11月07 12:48:26
 */
@Component
@ConfigurationProperties(prefix = "nginx")
public class NginxProperties {

    /**
     * 是否需要启动 nginx
     */
    private Boolean enable;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "NginxProperties["
                + " enable=" + enable
                + " ]";
    }
}
