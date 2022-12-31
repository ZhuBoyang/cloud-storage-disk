package online.yangcloud.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zhuboyang
 * @since 2022年04月04 21:07:15
 */
@Component
@ConfigurationProperties(prefix = "website")
public class NavigationProperties {

    /**
     * 网站路由
     */
    private Map<String, String> navigation;

    public Map<String, String> getNavigation() {
        return navigation;
    }

    public void setNavigation(Map<String, String> navigation) {
        this.navigation = navigation;
    }

    @Override
    public String toString() {
        return "NavigationProperties["
                + " navigation=" + navigation
                + " ]";
    }
}
