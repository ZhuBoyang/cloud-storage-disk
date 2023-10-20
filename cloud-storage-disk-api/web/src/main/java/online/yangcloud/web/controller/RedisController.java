package online.yangcloud.web.controller;

import online.yangcloud.common.model.VideoMetadata;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhuboyang
 * @since 2023年10月16 21:54:28
 */
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/send")
    public void send() {
        redisTemplate.convertAndSend("channel", VideoMetadata.initial());
    }

}
