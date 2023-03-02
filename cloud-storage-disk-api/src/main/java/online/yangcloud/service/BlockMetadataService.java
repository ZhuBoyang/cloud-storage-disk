package online.yangcloud.service;

import online.yangcloud.model.ao.file.BlockCheckExistRequest;
import online.yangcloud.model.po.BlockMetadata;

import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年01月03 12:12:28
 */
public interface BlockMetadataService {

    /**
     * 记录文件块元数据
     *
     * @param block 文件块
     * @return result
     */
    BlockMetadata insertOne(BlockMetadata block);

    /**
     * 根据 hash 查询文件块
     *
     * @param hash 文件块 hash
     * @return result
     */
    BlockMetadata queryByHash(String hash);

    /**
     * 根据 hash 查询文件块列表
     *
     * @param hashList 文件块 hash
     * @return result
     */
    List<BlockMetadata> queryByHashList(List<String> hashList);

    /**
     * 根据 hash 值检测文件块是否已入库
     *
     * @param existRequest 请求参数
     * @return result
     */
    List<Boolean> checkBlocksExist(BlockCheckExistRequest existRequest);

}
