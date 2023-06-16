package online.yangcloud.service.meta.impl;

import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.common.resultcode.AppResultCode;
import online.yangcloud.enumration.YesOrNoEnum;
import online.yangcloud.exception.BusinessException;
import online.yangcloud.mapper.FileBlockMapper;
import online.yangcloud.model.FileBlock;
import online.yangcloud.service.meta.FileBlockService;
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
        // 分批次入库
        int maxCount = AppConstants.FileMetadata.SINGLE_SAVE_MAX_COUNT;
        int cycleCount = fileBlocks.size() / maxCount;
        if (fileBlocks.size() % maxCount != 0) {
            cycleCount++;
        }
        if (fileBlocks.size() > 0) {
            for (int i = 0; i < cycleCount; i++) {
                List<FileBlock> currentCopied = fileBlocks.subList(i * maxCount, Math.min((i + 1) * maxCount, fileBlocks.size()));
                int updateResult = fileBlockMapper.insertBatchWithPk(currentCopied);
                if (updateResult != currentCopied.size()) {
                    throw new BusinessException(AppResultCode.FAILURE.getMessage());
                }
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
