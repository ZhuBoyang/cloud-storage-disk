package online.yangcloud.common.config;

import online.yangcloud.common.properties.redis.RedisConfigProperty;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置，用于设置分布式锁
 *
 * @author zhuboyang
 * @since 2022/12/31 11:55
 */
@Configuration
public class RedissonConfig {

    @Autowired
    private RedisConfigProperty redisConfigProperty;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisConfigProperty.getHost() + ":" + redisConfigProperty.getPort())
                .setPassword(redisConfigProperty.getPassword())
                .setTimeout(redisConfigProperty.getTimeout())
                .setRetryAttempts(3)
                .setRetryInterval(1000)
                // **此项务必设置为redisson解决之前bug的timeout问题关键*****
                .setPingConnectionInterval(1000)
                .setDatabase(redisConfigProperty.getDatabase());
        return Redisson.create(config);
    }

}
