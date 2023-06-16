package online.yangcloud.service.meta;

import online.yangcloud.enumration.FileTypeEnum;
import online.yangcloud.model.FileMetadata;
import online.yangcloud.model.vo.PagerView;

import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年06月10 22:15:04
 */
public interface FileMetadataService {

    /**
     * 记录文件元数据
     *
     * @param file 文件元数据
     */
    void insertWidthPrimaryKey(FileMetadata file);

    /**
     * 批量记录文件元数据
     *
     * @param files 文件元数据列表
     */
    void batchInsert(List<FileMetadata> files);

    /**
     * 批量删除文件
     *
     * @param ids    文件 id 列表
     * @param userId 文件所属用户 id
     */
    void batchRemove(List<String> ids, String userId);

    /**
     * 更新文件
     *
     * @param file 文件元数据
     */
    void updateById(FileMetadata file);

    /**
     * 批量更新文件元数据
     *
     * @param files 文件元数据列表
     */
    void batchUpdate(List<FileMetadata> files);

    /**
     * 查询文件元数据
     *
     * @param id 文件 id
     * @return 文件元数据
     */
    FileMetadata queryById(String id);

    /**
     * 查询文件元数据
     *
     * @param id     文件 id
     * @param userId 用户 id
     * @return 文件元数据
     */
    FileMetadata queryById(String id, String userId);

    /**
     * 查询文件元数据
     *
     * @param pid    父级文件 id
     * @param userId 用户 id
     * @return 文件元数据
     */
    FileMetadata queryByPid(String pid, String userId);

    /**
     * 批量查询文件元数据
     *
     * @param fileIds 文件 id 列表
     * @param userId  用户 id
     * @return 文件元数据列表
     */
    List<FileMetadata> queryListByIds(List<String> fileIds, String userId);

    /**
     * 批量查询文件元数据
     *
     * @param pid    文件 id 列表
     * @param userId 用户 id
     * @return 文件元数据列表
     */
    List<FileMetadata> queryListByPid(String pid, String userId);

    /**
     * 查询目录下所有的文件
     *
     * @param pid    目录 id
     * @param userId 用户 id
     * @return 子文件列表
     */
    List<FileMetadata> queryChildDirsListByPid(String pid, String userId);

    /**
     * 查询目录下所有的文件
     *
     * @param pid       目录 id
     * @param userId    用户 id
     * @param isOnlyDir 是否只查询目录
     * @return 子文件列表
     */
    List<FileMetadata> queryChildListByPid(String pid, String userId, Boolean isOnlyDir);

    /**
     * 查询以文件名为前缀的所有文件列表
     *
     * @param pid      父级目录文件 id
     * @param fileName 待查询文件名（不带文件后缀）
     * @param type     文件类型
     * @return result
     */
    List<FileMetadata> queryLikePrefix(String pid, String fileName, FileTypeEnum type);

    /**
     * 查询文件列表
     *
     * @param pid       父级文件 id
     * @param name      文件名
     * @param userId    文件拥有者 id
     * @param isOnlyDir 是否只查询目录
     * @param pageIndex 当前所在页码
     * @param pageSize  每页显示的数据量
     * @return 文件列表
     */
    PagerView<FileMetadata> queryFilesInPager(String pid, String name, String userId, Boolean isOnlyDir,
                                              Integer pageIndex, Integer pageSize);

}
