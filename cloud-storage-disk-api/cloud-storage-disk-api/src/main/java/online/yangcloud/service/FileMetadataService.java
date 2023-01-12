package online.yangcloud.service;

import online.yangcloud.common.ResultBean;
import online.yangcloud.enumration.FileTypeEnum;
import online.yangcloud.model.ao.file.FileSearchRequest;
import online.yangcloud.model.po.FileMetadata;
import online.yangcloud.model.vo.file.FileBreadView;
import online.yangcloud.model.vo.file.FileMetadataView;

import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年01月03 11:08:39
 */
public interface FileMetadataService {

    /**
     * 入库文件元数据
     *
     * @param file 文件元数据
     * @return result
     */
    FileMetadata insertOne(FileMetadata file);

    /**
     * 新建文件夹
     *
     * @param pid      父级目录 id
     * @param fileName 文件夹名称
     * @return result
     */
    FileMetadataView mkdir(String pid, String fileName);

    /**
     * 批量删除文件及文件夹
     *
     * @param fileIds 文件或文件夹 id 列表
     * @return result
     */
    ResultBean<?> batchDeleteFile(List<String> fileIds);

    /**
     * 查询当前所在目录的文件面包屑导航
     *
     * @param id 当前所在目录的文件 id
     * @return result
     */
    List<FileBreadView> queryFileBreads(String id);

    /**
     * 查询文件元数据
     *
     * @param id 文件 id
     * @return result
     */
    FileMetadata queryById(String id);

    /**
     * 分页查询所有文件及文件夹
     *
     * @param searchRequest 请求参数
     * @return result
     */
    List<FileMetadataView> queryFiles(FileSearchRequest searchRequest);

    /**
     * 查询以文件名为前缀的所有文件列表
     *
     * @param pid      父级目录文件 id
     * @param fileName 待查询文件名（不带文件后缀）
     * @param type     文件类型
     * @return result
     */
    List<FileMetadata> queryLikePrefix(String pid, String fileName, FileTypeEnum type);

}
