package online.yangcloud.web.service;

import online.yangcloud.common.model.FileMetadata;

import java.util.List;
import java.util.Map;

/**
 * @author zhuboyang
 * @since 2023年09月07 10:16:31
 */
public interface ThumbnailService {

    /**
     * 获取文件的缩略图并记录
     *
     * @param file 文件元数据
     */
    void thumbnail(FileMetadata file);

    /**
     * 查询文件的缩略图
     *
     * @param file 文件元数据
     * @return 缩略图地址
     */
    String queryThumbnail(FileMetadata file);

    /**
     * 批量查询文件的缩略图
     *
     * @param files 文件元数据列表
     * @return 缩略图地址列表
     */
    Map<String, String> queryThumbnails(List<FileMetadata> files);

}
