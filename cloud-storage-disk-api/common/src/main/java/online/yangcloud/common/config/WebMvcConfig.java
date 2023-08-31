package online.yangcloud.common.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import online.yangcloud.common.tools.SystemTools;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        ClassPathResource classpath = new ClassPathResource("classpath:static/");
        List<File> files = new ArrayList<>(Arrays.asList(FileUtil.ls(classpath.getUrl().getPath())));
        for (File file : files) {
            String fileName = file.getName();
            registry.addResourceHandler("/" + fileName + "/**").addResourceLocations("classpath:static/" + fileName + "/");
        }
    }

}
