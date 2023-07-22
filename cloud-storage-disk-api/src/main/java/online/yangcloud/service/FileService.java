package online.yangcloud.service;

import online.yangcloud.model.User;
import online.yangcloud.model.ao.file.DirLooker;
import online.yangcloud.model.ao.file.FileSearcher;
import online.yangcloud.model.ao.file.FileUploader;
import online.yangcloud.model.ao.file.TrashQuery;
import online.yangcloud.model.vo.PagerView;
import online.yangcloud.model.vo.file.BreadsView;
import online.yangcloud.model.vo.file.FileMetadataView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年05月17 10:47:13
 */
public interface FileService {

    /**
     * 检测文件块是否已存在库中
     *
     * @param uploader 文件块上传参数
     * @return 检测结果
     */
    Integer checkBlockExist(FileUploader uploader);

    /**
     * 上传文件块
     *
     * @param uploader 文件块元数据
     * @return 上传的结果
     */
    Integer blockUpload(FileUploader uploader);

    /**
     * 合并文件
     *
     * @param identifier 文件唯一识别码
     * @param user       当前登录用户
     * @return 文件元数据
     * @throws IOException IOException
     */
    FileMetadataView fileMerge(String identifier, User user) throws IOException;

    /**
     * 初始化用户的文件根目录
     *
     * @param userId 用户 id
     */
    void initialUserRoot(String userId);

    /**
     * 新建文件夹
     *
     * @param pid    父级文件 id
     * @param name   文件夹名
     * @param userId 当前登录用户 id
     * @return 文件夹元数据
     */
    FileMetadataView mkdir(String pid, String name, String userId);

    /**
     * 批量删除文件及文件夹
     *
     * @param ids  文件或文件夹 id 列表
     * @param user 当前登录用户
     */
    void batchDeleteFile(List<String> ids, User user);

    /**
     * 批量复制文件
     *
     * @param sourcesIds 待复制文件列表
     * @param targetId   目标目录
     * @param user       当前登录用户
     */
    void batchCopy(List<String> sourcesIds, String targetId, User user);

    /**
     * 批量复制文件
     *
     * @param sourcesIds 待复制文件列表
     * @param targetId   目标目录
     * @param userId     当前登录用户 id
     */
    void batchMove(List<String> sourcesIds, String targetId, String userId);

    /**
     * 修改文件名
     *
     * @param id   文件 id
     * @param name 文件名
     */
    void rename(String id, String name);

    /**
     * 查询文件元数据
     *
     * @param id     文件 id
     * @param userId 用户 id
     * @return 文件元数据
     */
    FileMetadataView queryById(String id, String userId);

    /**
     * 查询面包屑导航数据
     *
     * @param id     当前所在目录的文件 id
     * @param userId 当前登录的用户 id
     * @return 面包屑导航数据
     */
    List<BreadsView> queryBreads(String id, String userId);

    /**
     * 分段查询已删除的文件
     *
     * @param query  查询条件
     * @param userId 文件所属用户 id
     * @return 文件列表
     */
    PagerView<FileMetadataView> queryDeletedFiles(TrashQuery query, String userId);

    /**
     * 分页搜索某一目录下次一层级的所有文件及文件夹
     *
     * @param searcher 分页参数
     * @param userId   当前登录的用户 id
     * @return 次一层级的所有文件及文件夹列表
     */
    PagerView<FileMetadataView> queryFiles(FileSearcher searcher, String userId);

    /**
     * 查询目录下所有的文件夹
     *
     * @param looker 查询参数
     * @param userId 当前登录的用户 id
     * @return 文件夹列表
     */
    List<FileMetadataView> queryDirs(DirLooker looker, String userId);

    /**
     * 计算文件名后缀数字（检查是否有重复的文件名）
     *
     * @param pid      父级目录 id
     * @param name     文件名
     * @param fileType 文件类型
     * @return 计算出的文件名后缀数字
     */
    Integer calculateSuffixNumber(String pid, String name, Integer fileType);

    /**
     * 计算文件名（检查是否有重复的文件名，如果有，就在文件名后添加后缀数字）
     *
     * @param pid      父级目录 id
     * @param name     文件名
     * @param fileType 文件类型
     * @return 计算后的文件名
     */
    String calculateName(String pid, String name, Integer fileType);

    /**
     * 检测是否有重复的文件名称
     *
     * @param id       文件 id
     * @param pid      父级目录 id
     * @param name     文件名
     * @param fileType 文件类型
     * @return 检测结果
     */
    Boolean validDuplicatedName(String id, String pid, String name, Integer fileType);

    /**
     * 文件下载
     *
     * @param id       文件 id
     * @param response 响应
     */
    void download(String id, HttpServletResponse response);

}
