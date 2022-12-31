package online.yangcloud;

import cn.org.atool.fluent.mybatis.spring.MapperFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * the application runner of cloud storage disk
 *
 * @author zhuboyang
 * @since 2022/12/12 17:13
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
