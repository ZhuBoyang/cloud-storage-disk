package online.yangcloud.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhuboyang
 * @since 2022年08月24 16:10:49
 */
@ConfigurationProperties(prefix = "project")
@Component
public class ProjectProperties {

    /**
     * 项目根目录
     */
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "ProjectProperties["
                + " artifactId=" + path
                + " ]";
    }
}
