package online.yangcloud.web.config;

import online.yangcloud.common.common.AppConstants;
import online.yangcloud.web.listener.PreviewProcessorListener;
import online.yangcloud.web.listener.FileAnalysisListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author zhuboyang
 * @since 2023年10月19 13:15:49
 */
@Configuration
public class BeanConfig {

    @Bean
    public MessageListenerAdapter previewMessageListener() {
        return new MessageListenerAdapter(new PreviewProcessorListener());
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(previewMessageListener(), new ChannelTopic(AppConstants.Topic.PREVIEW));
        return container;
    }

}
