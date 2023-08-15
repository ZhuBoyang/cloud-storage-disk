package online.yangcloud.common.properties.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhuboyang
 * @since 2022年03月11 10:10:55
 */
@Component
@ConfigurationProperties("spring.redis.lettuce")
public class RedisLettuceConfigProperty {

    private Integer shutdownTimeout;

    @Autowired
    private RedisPoolConfigProperty redisPoolConfig;

    public Integer getShutdownTimeout() {
        return shutdownTimeout;
    }

    public void setShutdownTimeout(Integer shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }

    public RedisPoolConfigProperty getRedisPoolConfig() {
        return redisPoolConfig;
    }

    public void setRedisPoolConfig(RedisPoolConfigProperty redisPoolConfig) {
        this.redisPoolConfig = redisPoolConfig;
    }

    @Override
    public String toString() {
        return "RedisLettuceConfig["
                + " shutdownTimeout=" + shutdownTimeout + ","
                + " redisPoolConfig=" + redisPoolConfig
                + " ]";
    }
}
