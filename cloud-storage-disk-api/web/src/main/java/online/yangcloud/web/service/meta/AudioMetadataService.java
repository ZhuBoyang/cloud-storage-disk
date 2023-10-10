package online.yangcloud.web.service.meta;

import online.yangcloud.common.model.AudioMetadata;

import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年09月22 20:09:32
 */
public interface AudioMetadataService {

    /**
     * 添加音频信息记录
     *
     * @param metadata 音频元数据
     */
    void addAudioRecord(AudioMetadata metadata);

    /**
     * 查询音频文件的音频元数据
     *
     * @param id 文件 id
     * @return 音频元数据
     */
    AudioMetadata queryAudioByFileId(String id);

    /**
     * 批量查询音频文件的音频元数据
     *
     * @param fileIds 文件 id 列表
     * @return 音频元数据
     */
    List<AudioMetadata> queryAudiosByFileIds(List<String> fileIds);

}
