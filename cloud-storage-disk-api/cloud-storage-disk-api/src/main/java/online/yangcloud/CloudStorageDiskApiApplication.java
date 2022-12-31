package online.yangcloud;

import cn.org.atool.fluent.mybatis.spring.MapperFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author zhuboyang
 * @since 2022/12/31 11:53
 */
@SpringBootApplication
public class CloudStorageDiskApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudStorageDiskApiApplication.class, args);
    }

    @Bean
    public MapperFactory mapperFactory() {
        return new MapperFactory();
    }

}
