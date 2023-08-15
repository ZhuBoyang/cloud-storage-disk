package online.yangcloud.web.service.meta;

import online.yangcloud.common.model.FileBlock;

import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年06月10 22:19:33
 */
public interface FileBlockService {

    /**
     * 批量记录文件与文件块的关联
     *
     * @param fileBlocks 关联关系列表
     */
    void batchInsert(List<FileBlock> fileBlocks);

    /**
     * 查询文件与文件块的关联信息
     *
     * @param fileId 文件 id
     * @return 关联信息
     */
    List<FileBlock> queryBlocks(String fileId);

}
