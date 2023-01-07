package online.yangcloud.service;

import online.yangcloud.model.ao.BlockUpload;
import online.yangcloud.model.vo.file.FileMetadataView;

import java.io.IOException;

/**
 * @author zhuboyang
 * @since 2022年12月31 21:15:24
 */
public interface FileBlockService {

    /**
     * 记录文件块数据
     *
     * @param upload 文件块参数
     */
    void recordBlockMetadata(BlockUpload upload);

    /**
     * 上传文件块
     *
     * @param upload 文件块参数
     */
    void uploadFileBlock(BlockUpload upload) throws IOException;

    /**
     * 合并文件
     *
     * @param identifier 文件块识别码
     */
    FileMetadataView mergeFile(String identifier, String hash);

}
