package online.yangcloud.web.service.meta;

import online.yangcloud.common.model.DocumentMetadata;

import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年10月20 11:14:54
 */
public interface DocumentMetadataService {

    /**
     * 添加 office 文件信息记录
     *
     * @param metadata 文档元数据
     */
    void addDocumentRecord(DocumentMetadata metadata);

    /**
     * 查询 office 文件的文档元数据
     *
     * @param id 文件 id
     * @return 文档元数据
     */
    DocumentMetadata queryDocumentByFileId(String id);

    /**
     * 批量查询 office 文件的文档元数据
     *
     * @param fileIds 文件 id 列表
     * @return 文档元数据
     */
    List<DocumentMetadata> queryDocumentsByFileIds(List<String> fileIds);

}
