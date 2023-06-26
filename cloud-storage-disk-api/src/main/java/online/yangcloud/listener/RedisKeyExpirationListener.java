package online.yangcloud.listener;

import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.service.UserService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhuboyang
 * @since 2023年06月25 11:06:53
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Resource
    private UserService userService;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 监听到过期消息
     *
     * @param message key
     * @param pattern 消息事件
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if (expiredKey.startsWith(AppConstants.User.SPACE_UPDATE)) {
            // 更新用户账户空间
            userService.updateUserSpace(expiredKey);
        }
    }

}