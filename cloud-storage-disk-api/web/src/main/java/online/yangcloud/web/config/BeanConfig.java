package online.yangcloud.web.config;

import online.yangcloud.web.listener.FileAnalysisListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author zhuboyang
 * @since 2023年10月16 22:28:40
 */
@Configuration
public class BeanConfig {

    @Bean
    public MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new FileAnalysisListener());
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListener(), new ChannelTopic("channel"));
        return container;
    }

}
