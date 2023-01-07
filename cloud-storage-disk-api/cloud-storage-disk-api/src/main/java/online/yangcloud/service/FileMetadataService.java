package online.yangcloud.service;

import online.yangcloud.model.ao.file.FileSearchRequest;
import online.yangcloud.model.po.FileMetadata;
import online.yangcloud.model.vo.file.FileBreadView;
import online.yangcloud.model.vo.file.FileMetadataView;
import online.yangcloud.utils.PagerHelper;

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

}
