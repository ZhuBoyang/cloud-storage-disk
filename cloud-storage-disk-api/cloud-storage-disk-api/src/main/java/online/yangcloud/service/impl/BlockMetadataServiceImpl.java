package online.yangcloud.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.exception.BusinessException;
import online.yangcloud.model.ao.file.BlockCheckExistRequest;
import online.yangcloud.model.ao.file.BlockUpload;
import online.yangcloud.model.mapper.BlockMetadataMapper;
import online.yangcloud.model.po.BlockMetadata;
import online.yangcloud.service.BlockMetadataService;
import online.yangcloud.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhuboyang
 * @since 2023年01月03 12:12:41
 */
@Service
public class BlockMetadataServiceImpl implements BlockMetadataService {

    @Autowired
    private BlockMetadataMapper blockMetadataMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public BlockMetadata insertOne(BlockMetadata block) {
        int updateResult = blockMetadataMapper.insertWithPk(block);
        if (updateResult == 0) {
            throw new BusinessException("文件块记录失败，请重试");
        }
        return block;
    }

    @Override
    public BlockMetadata queryByHash(String hash) {
        return blockMetadataMapper.findOne(blockMetadataMapper.query().where.hash().eq(hash).end());
    }

    @Override
    public List<BlockMetadata> queryByHashList(List<String> hashList) {
        return blockMetadataMapper.listEntity(blockMetadataMapper.query().where.hash().in(hashList).end());
    }

    @Override
    public List<Boolean> checkBlocksExist(BlockCheckExistRequest existRequest) {
        // 解析要检测的 hash 列表
        List<String> hashList = CharSequenceUtil.split(existRequest.getHashList(), StrUtil.COMMA);

        // 查询 hash 块是否存在
        List<BlockMetadata> blocks = blockMetadataMapper.listEntity(blockMetadataMapper.query().where.hash().in(hashList).end());
        Map<String, BlockMetadata> blocksMap = blocks.stream().collect(Collectors.toMap(BlockMetadata::getHash, block -> block));

        // 检测 hash 列表中的 hash 块是否已入库，并封装结果
        List<Boolean> exists = new ArrayList<>();
        for (int i = 0; i < hashList.size(); i++) {
            String hash = hashList.get(i);
            boolean exist = ObjUtil.isNotNull(blocksMap.get(hash));
            if (exist) {
                BlockUpload upload = new BlockUpload()
                        .setPid(existRequest.getPid())
                        .setFileName(existRequest.getFileName())
                        .setHash(hash)
                        .setIdentifier(existRequest.getIdentifier())
                        .setBlockIndex(i)
                        .setBlockSize(blocksMap.get(hash).getBlockSize())
                        .setBlockCount(hashList.size())
                        .setFileSize(existRequest.getFileSize())
                        .setShardingSize(existRequest.getShardingSize())
                        .setShard(existRequest.getShard());
                String redisValue = JSONUtil.toJsonStr(upload);
                redisUtil.zSetAdd(AppConstants.FILE_BLOCK_UPLOAD_PREFIX + existRequest.getIdentifier(), redisValue, (double) i);
            }
            exists.add(exist);
        }
        return exists;
    }

}
