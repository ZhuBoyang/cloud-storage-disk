package online.yangcloud.model;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.enumration.FileTypeEnum;
import online.yangcloud.enumration.YesOrNoEnum;
import online.yangcloud.model.ao.file.BlockUploader;

import java.util.Date;

/**
 * @author zhuboyang
 * @since 2022年12月31 21:20:20
 */
@FluentMybatis
public class FileMetadata extends RichEntity {

    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 父级目录 id
     */
    private String pid;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件存储路径
     */
    private String path;

    /**
     * 文件 hash
     */
    private String hash;

    /**
     * 文件类型：0.文件；1.文件夹
     */
    private Integer type;

    /**
     * 文件后缀
     */
    private String ext;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 上传时间
     */
    private Date uploadTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 所有父级目录 id
     */
    private String ancestors;

    /**
     * 是否已删除
     */
    private Integer isDelete;

    /**
     * 用户 id
     */
    private String userId;

    /**
     * 初始化文件元数据
     *
     * @param fileId     文件 id
     * @param fileNumber 文件后缀数
     * @param fileHash   文件 hash
     * @param parent     父级目录元数据
     * @param uploader   文件块上传参数
     * @param user       操作人
     * @return 文件元数据
     */
    public static FileMetadata initial(String fileId, Integer fileNumber, String fileHash, FileMetadata parent,
                                              BlockUploader uploader, User user) {
        return new FileMetadata()
                .setId(fileId)
                .setPid(uploader.getPid())
                .setName(fileNumber == 0 ?
                        uploader.getFileName() : uploader.getFileName() + AppConstants.LEFT_BRACKET + fileNumber + AppConstants.RIGHT_BRACKET)
                .setHash(fileHash)
                .setExt(uploader.getExt())
                .setPath(AppConstants.FILE_UPLOAD_PATH + fileHash)
                .setType(FileTypeEnum.FILE.getCode())
                .setSize(uploader.getFileSize())
                .setAncestors(CharSequenceUtil.isBlank(parent.getAncestors()) ? parent.getId() : parent.getAncestors() + StrUtil.COMMA + parent.getId())
                .setUploadTime(DateUtil.date())
                .setUserId(user.getId());
    }

    /**
     * 初始化用户根目录
     *
     * @param userId 用户 id
     * @return result
     */
    public static FileMetadata initRoot(String userId) {
        return new FileMetadata()
                .setId(IdUtil.fastSimpleUUID())
                .setPid(CharSequenceUtil.EMPTY)
                .setName("全部")
                .setPath(CharSequenceUtil.EMPTY)
                .setHash(CharSequenceUtil.EMPTY)
                .setType(FileTypeEnum.DIR.getCode())
                .setExt(CharSequenceUtil.EMPTY)
                .setSize(0L)
                .setUploadTime(DateUtil.date())
                .setUpdateTime(DateUtil.date())
                .setIsDelete(YesOrNoEnum.NO.getCode())
                .setUserId(userId)
                .setAncestors(CharSequenceUtil.EMPTY);
    }

    public String getId() {
        return id;
    }

    public FileMetadata setId(String id) {
        this.id = id;
        return this;
    }

    public String getPid() {
        return pid;
    }

    public FileMetadata setPid(String pid) {
        this.pid = pid;
        return this;
    }

    public String getName() {
        return name;
    }

    public FileMetadata setName(String name) {
        this.name = name;
        return this;
    }

    public String getPath() {
        return path;
    }

    public FileMetadata setPath(String path) {
        this.path = path;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public FileMetadata setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public FileMetadata setType(Integer type) {
        this.type = type;
        return this;
    }

    public String getExt() {
        return ext;
    }

    public FileMetadata setExt(String ext) {
        this.ext = ext;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public FileMetadata setSize(Long size) {
        this.size = size;
        return this;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public FileMetadata setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public FileMetadata setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getAncestors() {
        return ancestors;
    }

    public FileMetadata setAncestors(String ancestors) {
        this.ancestors = ancestors;
        return this;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public FileMetadata setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public FileMetadata setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public String toString() {
        return "FileMetadata["
                + " id=" + id + ","
                + " pid=" + pid + ","
                + " name=" + name + ","
                + " path=" + path + ","
                + " hash=" + hash + ","
                + " type=" + type + ","
                + " ext=" + ext + ","
                + " size=" + size + ","
                + " uploadTime=" + uploadTime + ","
                + " updateTime=" + updateTime + ","
                + " ancestors=" + ancestors + ","
                + " isDelete=" + isDelete + ","
                + " userId=" + userId
                + " ]"
                + " "
                + super.toString();
    }
}
