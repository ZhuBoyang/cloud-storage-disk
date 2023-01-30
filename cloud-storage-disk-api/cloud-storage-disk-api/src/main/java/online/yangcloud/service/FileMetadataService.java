package online.yangcloud.service;

import online.yangcloud.common.ResultBean;
import online.yangcloud.enumration.FileTypeEnum;
import online.yangcloud.model.ao.file.FileRenameRequest;
import online.yangcloud.model.ao.file.FileSearchRequest;
import online.yangcloud.model.po.FileMetadata;
import online.yangcloud.model.po.User;
import online.yangcloud.model.vo.file.FileBreadView;
import online.yangcloud.model.vo.file.FileMetadataView;

import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年01月03 11:08:39
 */
public interface FileMetadataService {

    /**
     * 初始化用户根目录
     *
     * @param userId 当前登录的用户 id
     */
    void initUserFile(String userId);

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
     * @param user     当前登录的用户
     * @return result
     */
    FileMetadataView mkdir(String pid, String fileName, User user);

    /**
     * 批量删除文件及文件夹
     *
     * @param fileIds 文件或文件夹 id 列表
     * @param user    当前登录的用户
     * @return result
     */
    ResultBean<?> batchDeleteFile(List<String> fileIds, User user);

    /**
     * 批量移动文件
     *
     * @param sources 待移动文件列表
     * @param target  目标目录
     * @param user    当前登录的用户
     * @return result
     */
    ResultBean<?> batchMoveFiles(List<String> sources, String target, User user);

    /**
     * 批量复制文件
     *
     * @param sources 待复制文件列表
     * @param target  目标目录
     * @param user    当前登录的用户
     * @return result
     */
    ResultBean<?> batchCopyFiles(List<String> sources, String target, User user);

    /**
     * 重命名文件
     *
     * @param renameRequest 要重命名的文件
     * @param user          当前登录的用户
     * @return result
     */
    FileMetadata rename(FileRenameRequest renameRequest, User user);

    /**
     * 查询文件的播放地址（媒体文件为播放地址，office 文件为预览地址）
     *
     * @param fileId 文件 id
     * @return 播放地址
     */
    String findPlayUrl(String fileId);

    /**
     * 查询当前所在目录的文件面包屑导航
     *
     * @param id   当前所在目录的文件 id
     * @param user 当前登录的用户
     * @return result
     */
    List<FileBreadView> queryFileBreads(String id, User user);

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
     * @param user          当前登录的用户
     * @return result
     */
    List<FileMetadataView> queryFiles(FileSearchRequest searchRequest, User user);

    /**
     * 查询目录下所有的文件与文件夹
     *
     * @param fileId 目录文件 id
     * @return result
     */
    List<FileMetadata> queryChildFiles(String fileId);

    /**
     * 查询以文件名为前缀的所有文件列表
     *
     * @param pid      父级目录文件 id
     * @param fileName 待查询文件名（不带文件后缀）
     * @param type     文件类型
     * @param user     当前登录的用户
     * @return result
     */
    List<FileMetadata> queryLikePrefix(String pid, String fileName, FileTypeEnum type, User user);

    /**
     * 查询文件夹的面包屑
     *
     * @param parentId 父级目录 id
     * @return result
     */
    List<FileBreadView> queryDirBreads(String parentId);

    /**
     * 查询目录下次一层的所有文件夹
     *
     * @param parentId 目录 id
     * @param size     每次查询的数据量
     * @param user     当前登录的用户
     * @return result
     */
    List<FileMetadata> queryDirs(String parentId, Integer size, User user);

}
