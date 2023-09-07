package online.yangcloud.web.service.meta;

import online.yangcloud.common.model.VideoMetadata;

import java.util.List;

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

    /**
     * 查询视频文件的视频元数据
     *
     * @param id 文件 id
     * @return 视频元数据
     */
    VideoMetadata queryVideoByFileId(String id);

    /**
     * 批量查询视频文件的视频元数据
     *
     * @param fileIds 文件 id 列表
     * @return 视频元数据
     */
    List<VideoMetadata> queryVideosByFileIds(List<String> fileIds);

}
