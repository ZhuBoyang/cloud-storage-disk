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
     * 批量查询文件的预览信息
     *
     * @param files 文件元数据列表
     * @return 预览信息
     */
    Map<String, Object> queryMetadata(List<FileMetadata> files);

    /**
     * 查询媒体文件解析后的元数据
     *
     * @param fileId 媒体文件 id
     * @param ext    文件后缀
     * @return 元数据
     */
    Object queryMediaMetadata(String fileId, String ext);

}
