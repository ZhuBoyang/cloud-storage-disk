package online.yangcloud.common.config;

import online.yangcloud.common.tools.SystemTools;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web mvc配置，用以识别静态资源文件
 *
 * @author zhuboyang
 * @since 2022/12/31 11:57
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("file:" + SystemTools.systemPath());
        registry.addResourceHandler("/background/**").addResourceLocations("classpath:static/background/");
        registry.addResourceHandler("/center/**").addResourceLocations("classpath:static/center/");
        registry.addResourceHandler("/file/**").addResourceLocations("classpath:static/file/");
        registry.addResourceHandler("/general/**").addResourceLocations("classpath:static/general/");
        registry.addResourceHandler("/icons/**").addResourceLocations("classpath:static/icons/");
        registry.addResourceHandler("/player/**").addResourceLocations("classpath:static/player/");
    }

}
