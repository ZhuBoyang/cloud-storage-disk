package online.yangcloud.web.listener;

import cn.hutool.json.JSONUtil;
import online.yangcloud.common.model.VideoMetadata;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * @author zhuboyang
 * @since 2023年10月16 21:41:14
 */
@Component
public class FileAnalysisListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("接收数据:" + JSONUtil.toBean(new String(message.getBody()), VideoMetadata.class));
        System.out.println("订阅频道:" + new String(message.getChannel()));
    }

}
