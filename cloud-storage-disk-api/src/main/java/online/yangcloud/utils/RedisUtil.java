package online.yangcloud.utils;

import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.constants.UserConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuby
 * @since 2021/4/6 22:33
 */
@Component
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 设置redis值
     *
     * @param redisKey   redisKey
     * @param redisValue redisValue
     */
    public void set(String redisKey, String redisValue) {
        redisTemplate.opsForValue().set(redisKey, redisValue);
    }

    /**
     * 查询redis值
     *
     * @param redisKey redisKey
     * @return 值
     */
    public String get(String redisKey) {
        logger.info("获取redis key[{}]", redisKey);
        return redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 值+1
     *
     * @param redisKey redisKey
     * @return 增加后的值
     */
    public Long inCre(String redisKey) {
        return redisTemplate.opsForValue().increment(redisKey, 1L);
    }

    /**
     * 设置redis值
     *
     * @param redisKey   redisKey
     * @param redisValue redisValue
     */
    public void expire(String redisKey, String redisValue) {
        expire(redisKey, redisValue, UserConstants.LOGIN_SESSION_EXPIRE_TIME);
    }

    /**
     * 设置redis过期
     *
     * @param redisKey redisKey
     * @param seconds  过期时间
     */
    public void expire(String redisKey, Integer seconds) {
        logger.info("开始设置redis key[{}]，并添加过期时间[{}]", redisKey, seconds);
        expire(redisKey, StrUtil.EMPTY, seconds);
    }

    /**
     * 设置redis的同时设置过期时间
     *
     * @param redisKey redisKey
     * @param value    redisValue
     * @param seconds  过期时间
     */
    public void expire(String redisKey, String value, Integer seconds) {
        logger.info("开始设置redis key[{}]，并添加过期时间[{}]", redisKey, seconds);
        expire(redisKey, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 设置redis的同时设置过期时间
     *
     * @param redisKey redisKey
     * @param value    redisValue
     * @param time     过期时间
     * @param timeUnit 时间类型
     */
    public void expire(String redisKey, String value, Integer time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(redisKey, value, time, timeUnit);
    }

    /**
     * 使用 setIfAbsent 进行设置，防止并发问题
     *
     * @param key      redisKey
     * @param value    redisValue
     * @param time     过期时间
     * @param timeUnit 过期时间单位
     */
    public Boolean setIfAbsent(String key, String value, Integer time, TimeUnit timeUnit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, time, timeUnit);
    }

    /**
     * 根据redisKey获取过期时间
     *
     * @param redisKey redisKey
     * @return 过期时间
     */
    public long getExpireTime(String redisKey) {
        Long expireTime = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        if (Optional.ofNullable(expireTime).isPresent()) {
            logger.info("redis key[{}]还未过期，获取到redis key的过期时间[{}]", redisKey, expireTime);
            return expireTime;
        }
        logger.info("redis key[{}]已过期", redisKey);
        return -2;
    }

    /**
     * 删除一个key
     *
     * @param redisKey redisKey
     */
    public void delete(String redisKey) {
        logger.info("删除redis[{}]", redisKey);
        redisTemplate.delete(redisKey);
    }

    /**
     * 存储有序集合
     *
     * @param redisKey   redisKey
     * @param redisValue 要存储的有序集合
     */
    public void setSet(String redisKey, Set<ZSetOperations.TypedTuple<String>> redisValue) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(redisKey, redisValue);
    }

    /**
     * 获取存储的有序集合
     *
     * @param redisKey redisKey
     * @return 集合
     */
    public Set<String> rangSetData(String redisKey) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Set<String> result = zSetOperations.range(redisKey, 0, -1);
        if (result == null || result.size() == 0) {
            return new HashSet<>();
        }
        return result;
    }

}
