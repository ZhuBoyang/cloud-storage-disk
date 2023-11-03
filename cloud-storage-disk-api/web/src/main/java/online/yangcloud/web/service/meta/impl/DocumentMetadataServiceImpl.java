package online.yangcloud.web.service.meta.impl;

import online.yangcloud.common.annotation.TimeConsuming;
import online.yangcloud.common.enumration.YesOrNoEnum;
import online.yangcloud.common.mapper.DocumentMetadataMapper;
import online.yangcloud.common.model.DocumentMetadata;
import online.yangcloud.common.tools.ExceptionTools;
import online.yangcloud.web.service.meta.DocumentMetadataService;
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
public class DocumentMetadataServiceImpl implements DocumentMetadataService {

    @Resource
    private DocumentMetadataMapper documentMetadataMapper;

    @Override
    public void addDocumentRecord(DocumentMetadata metadata) {
        ExceptionTools.updateError(documentMetadataMapper.insertWithPk(metadata));
    }

    @Override
    public DocumentMetadata queryDocumentByFileId(String id) {
        return documentMetadataMapper.findOne(documentMetadataMapper.query()
                .where.fileId().eq(id)
                .and.isDelete().eq(YesOrNoEnum.NO.code())
                .end());
    }

    @Override
    public List<DocumentMetadata> queryDocumentsByFileIds(List<String> fileIds) {
        return documentMetadataMapper.listEntity(documentMetadataMapper.query()
                .where.fileId().in(fileIds)
                .and.isDelete().eq(YesOrNoEnum.NO.code())
                .end());
    }

}
