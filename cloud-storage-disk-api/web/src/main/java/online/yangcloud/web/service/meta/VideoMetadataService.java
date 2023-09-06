package online.yangcloud.web.service.meta;

import online.yangcloud.common.model.VideoMetadata;

/**
 * @author zhuboyang
 * @since 2023年09月06 16:27:49
 */
public interface VideoMetadataService {

    /**
     * 添加视频信息记录
     *
     * @param metadata 视频元数据
     */
    void addVideoRecord(VideoMetadata metadata);

}
