package online.yangcloud.web.service.meta.impl;

import online.yangcloud.common.enumration.YesOrNoEnum;
import online.yangcloud.common.mapper.BlockMetadataMapper;
import online.yangcloud.common.model.BlockMetadata;
import online.yangcloud.common.utils.ExceptionTools;
import online.yangcloud.web.service.meta.BlockMetadataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年06月10 22:19:03
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BlockMetadataServiceImpl implements BlockMetadataService {

    @Resource
    private BlockMetadataMapper blockMetadataMapper;

    @Override
    public void insertBlock(BlockMetadata block) {
        int updateResult = blockMetadataMapper.insertWithPk(block);
        if (updateResult == 0) {
            ExceptionTools.businessLogger();
        }
    }

    @Override
    public BlockMetadata queryByHash(String hash) {
        return blockMetadataMapper.findOne(blockMetadataMapper.query()
                .where.hash().eq(hash).and.isDelete().eq(YesOrNoEnum.NO.code()).end());
    }

    @Override
    public List<BlockMetadata> queryListByIds(List<String> ids) {
        return blockMetadataMapper.listEntity(blockMetadataMapper.query()
                .where.id().in(ids).and.isDelete().eq(YesOrNoEnum.NO.code()).end());
    }

    @Override
    public List<BlockMetadata> queryListByHashList(List<String> hashList) {
        return blockMetadataMapper.listEntity(blockMetadataMapper.query()
                .where.hash().in(hashList).and.isDelete().eq(YesOrNoEnum.NO.code()).end());
    }

}
