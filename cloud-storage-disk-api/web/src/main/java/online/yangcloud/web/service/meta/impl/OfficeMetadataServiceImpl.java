package online.yangcloud.web.service.meta.impl;

import online.yangcloud.common.annotation.TimeConsuming;
import online.yangcloud.common.enumration.YesOrNoEnum;
import online.yangcloud.common.mapper.OfficeMetadataMapper;
import online.yangcloud.common.model.OfficeMetadata;
import online.yangcloud.common.tools.ExceptionTools;
import online.yangcloud.web.service.meta.OfficeMetadataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年10月20 11:15:07
 */
@Service
@Transactional(rollbackFor = Exception.class)
@TimeConsuming
public class OfficeMetadataServiceImpl implements OfficeMetadataService {

    @Resource
    private OfficeMetadataMapper officeMetadataMapper;

    @Override
    public void addOfficeRecord(OfficeMetadata metadata) {
        ExceptionTools.updateError(officeMetadataMapper.insertWithPk(metadata));
    }

    @Override
    public OfficeMetadata queryOfficeByFileId(String id) {
        return officeMetadataMapper.findOne(officeMetadataMapper.query()
                .where.fileId().eq(id)
                .and.isDelete().eq(YesOrNoEnum.NO.code())
                .end());
    }

    @Override
    public List<OfficeMetadata> queryOfficesByFileIds(List<String> fileIds) {
        return officeMetadataMapper.listEntity(officeMetadataMapper.query()
                .where.fileId().in(fileIds)
                .and.isDelete().eq(YesOrNoEnum.NO.code())
                .end());
    }

}
