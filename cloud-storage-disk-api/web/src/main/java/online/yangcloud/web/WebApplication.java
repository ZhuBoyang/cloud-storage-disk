package online.yangcloud.web;

import cn.org.atool.fluent.mybatis.spring.MapperFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhuboyang
 * @since 2023/8/14 21:57
 */
@SpringBootApplication
@ComponentScan(value = "online.yangcloud")
@MapperScan(basePackages = "online.yangcloud.common.mapper")
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    @Bean
    public MapperFactory mapperFactory() {
        return new MapperFactory();
    }

}
