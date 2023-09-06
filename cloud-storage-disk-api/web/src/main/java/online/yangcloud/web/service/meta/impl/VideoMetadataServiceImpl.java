package online.yangcloud.web.service.meta.impl;

import online.yangcloud.common.mapper.VideoMetadataMapper;
import online.yangcloud.common.model.VideoMetadata;
import online.yangcloud.common.tools.ExceptionTools;
import online.yangcloud.web.service.meta.VideoMetadataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author zhuboyang
 * @since 2023年09月06 16:28:03
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VideoMetadataServiceImpl implements VideoMetadataService {
    
    @Resource
    private VideoMetadataMapper videoMetadataMapper;
    
    @Override
    public void addVideoRecord(VideoMetadata metadata) {
        updateError(videoMetadataMapper.insert(metadata));
    }

    private void updateError(int updateResult) {
        if (updateResult == 0) {
            ExceptionTools.businessLogger();
        }
    }
    
}
