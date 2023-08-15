package online.yangcloud.web.service.meta;

import online.yangcloud.common.model.BlockMetadata;

import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年06月10 22:18:53
 */
public interface BlockMetadataService {

    /**
     * 记录文件块信息
     *
     * @param block 文件块信息
     */
    void insertBlock(BlockMetadata block);

    /**
     * 根据文件块 hash 查询文件块信息
     *
     * @param hash 文件块 hash
     * @return 文件块信息
     */
    BlockMetadata queryByHash(String hash);

    /**
     * 批量查询文件块列表
     *
     * @param ids 文件块 id 列表
     * @return 文件块列表
     */
    List<BlockMetadata> queryListByIds(List<String> ids);

    /**
     * 根据 hash 列表查询文件块列表
     *
     * @param hashList 文件块 hash 列表
     * @return 文件块列表
     */
    List<BlockMetadata> queryListByHashList(List<String> hashList);

}
