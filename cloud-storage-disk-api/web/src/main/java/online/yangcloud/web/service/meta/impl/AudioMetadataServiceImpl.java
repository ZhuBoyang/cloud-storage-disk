package online.yangcloud.web.service.meta.impl;

import online.yangcloud.common.enumration.YesOrNoEnum;
import online.yangcloud.common.mapper.AudioMetadataMapper;
import online.yangcloud.common.model.AudioMetadata;
import online.yangcloud.common.tools.ExceptionTools;
import online.yangcloud.web.service.meta.AudioMetadataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年09月22 20:10:59
 */
@Service
public class AudioMetadataServiceImpl implements AudioMetadataService {

    @Resource
    private AudioMetadataMapper audioMetadataMapper;

    @Override
    public void addAudioRecord(AudioMetadata metadata) {
        ExceptionTools.updateError(audioMetadataMapper.insertWithPk(metadata));
    }

    @Override
    public AudioMetadata queryAudioByFileId(String id) {
        return audioMetadataMapper.findOne(audioMetadataMapper.query()
                .where.fileId().eq(id)
                .and.isDelete().eq(YesOrNoEnum.NO.code())
                .end());
    }

    @Override
    public List<AudioMetadata> queryAudiosByFileIds(List<String> fileIds) {
        return audioMetadataMapper.listEntity(audioMetadataMapper.query()
                .where.fileId().in(fileIds)
                .and.isDelete().eq(YesOrNoEnum.NO.code())
                .end());
    }

}
