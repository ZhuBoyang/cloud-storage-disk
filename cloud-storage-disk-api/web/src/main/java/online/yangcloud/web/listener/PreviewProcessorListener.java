package online.yangcloud.web.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.model.FileMetadata;
import online.yangcloud.web.config.SpringContextHolder;
import online.yangcloud.web.service.ThumbnailService;
import online.yangcloud.web.service.impl.ThumbnailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * @author zhuboyang
 * @since 2023年10月19 13:14:45
 */
@Configuration
public class PreviewProcessorListener implements MessageListener {
    
    private static final Logger logger = LoggerFactory.getLogger(PreviewProcessorListener.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        FileMetadata file = JSONUtil.toBean(StrUtil.toString(message.toString()), FileMetadata.class);
        logger.info("接收到自频道【{}】要处理的文件信息 【{}】", message.getChannel(), file);
        String serviceName = ThumbnailServiceImpl.class.getSimpleName();
        String firstLetter = serviceName.substring(0, 1).toLowerCase();
        String lastLetters = serviceName.substring(1);
        ThumbnailService thumbnailService = SpringContextHolder.getBean(firstLetter + lastLetters);
        thumbnailService.thumbnail(file);
    }

}
