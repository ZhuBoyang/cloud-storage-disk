package online.yangcloud.properties.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhuboyang
 * @since 2022年03月11 09:56:03
 */
@Component
@ConfigurationProperties("spring.redis")
public class RedisConfigProperty {

    private String host;

    private Integer port;

    private Integer database;

    private String password;

    private Integer timeout;

    @Autowired
    private RedisLettuceConfigProperty redisLettuceConfig;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public RedisLettuceConfigProperty getRedisLettuceConfig() {
        return redisLettuceConfig;
    }

    public void setRedisLettuceConfig(RedisLettuceConfigProperty redisLettuceConfig) {
        this.redisLettuceConfig = redisLettuceConfig;
    }

    @Override
    public String toString() {
        return "RedisConfig["
                + " host=" + host + ","
                + " port=" + port + ","
                + " database=" + database + ","
                + " password=" + password + ","
                + " timeout=" + timeout + ","
                + " redisLettuceConfig=" + redisLettuceConfig
                + " ]";
    }
}
