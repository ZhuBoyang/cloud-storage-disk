package online.yangcloud.properties.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhuboyang
 * @since 2022年03月11 10:20:54
 */
@Component
@ConfigurationProperties("spring.redis.lettuce.pool")
public class RedisPoolConfigProperty {

    private Integer maxActive;

    private Integer maxIdle;

    private Integer maxWait;

    private Integer minIdle;

    public Integer getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    @Override
    public String toString() {
        return "RedisPoolConfig["
                + " maxActive=" + maxActive + ","
                + " maxIdle=" + maxIdle + ","
                + " maxWait=" + maxWait + ","
                + " minIdle=" + minIdle
                + " ]";
    }
}
