package online.yangcloud.web.service.meta.impl;

import online.yangcloud.common.enumration.YesOrNoEnum;
import online.yangcloud.common.mapper.VideoMetadataMapper;
import online.yangcloud.common.model.VideoMetadata;
import online.yangcloud.common.tools.ExceptionTools;
import online.yangcloud.web.service.meta.VideoMetadataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
        updateError(videoMetadataMapper.insertWithPk(metadata));
    }

    @Override
    public VideoMetadata queryVideoByFileId(String id) {
        return videoMetadataMapper.findOne(videoMetadataMapper.query()
                .where.fileId().eq(id)
                .and.isDelete().eq(YesOrNoEnum.NO.code())
                .end());
    }

    @Override
    public List<VideoMetadata> queryVideosByFileIds(List<String> fileIds) {
        return videoMetadataMapper.listEntity(videoMetadataMapper.query()
                .where.fileId().in(fileIds)
                .and.isDelete().eq(YesOrNoEnum.NO.code())
                .end());
    }

    private void updateError(int updateResult) {
        if (updateResult == 0) {
            ExceptionTools.businessLogger();
        }
    }

}
