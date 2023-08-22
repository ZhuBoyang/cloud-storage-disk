package online.yangcloud.web.service.meta.impl;

import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.enumration.YesOrNoEnum;
import online.yangcloud.common.mapper.FileBlockMapper;
import online.yangcloud.common.model.FileBlock;
import online.yangcloud.common.tools.ExceptionTools;
import online.yangcloud.web.service.meta.FileBlockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年06月10 22:19:43
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FileBlockServiceImpl implements FileBlockService {

    @Resource
    private FileBlockMapper fileBlockMapper;

    @Override
    public void batchInsert(List<FileBlock> fileBlocks) {
        while (!fileBlocks.isEmpty()) {
            List<FileBlock> tmp = fileBlocks.subList(0, Math.min(AppConstants.Batch.COUNT, fileBlocks.size()));
            fileBlocks = fileBlocks.subList(Math.min(AppConstants.Batch.COUNT, fileBlocks.size()), fileBlocks.size());
            int updateResult = fileBlockMapper.insertBatchWithPk(tmp);
            if (updateResult != tmp.size()) {
                ExceptionTools.businessLogger();
            }
        }
    }

    @Override
    public List<FileBlock> queryBlocks(String fileId) {
        return fileBlockMapper.listEntity(fileBlockMapper.query()
                .where.fileId().eq(fileId).and.isDelete().eq(YesOrNoEnum.NO.code()).end()
                .orderBy.index().asc().end());
    }

}
