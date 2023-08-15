package online.yangcloud.common.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

/**
 * 修复新版本tomcat在种cookie时无法校验域名开头的点的问题
 *
 * @author zhuboyang
 * @since 2022/12/31 11:55
 */
@Configuration
public class StarterContainerConfig {

//    @Bean
//    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
//        return (factory) -> factory.addContextCustomizers(
//                (context) -> context.setCookieProcessor(new LegacyCookieProcessor()));
//    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 单个数据大小
        factory.setMaxFileSize(DataSize.ofMegabytes(100));
        // 总上传数据大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
        return factory.createMultipartConfig();
    }

}
