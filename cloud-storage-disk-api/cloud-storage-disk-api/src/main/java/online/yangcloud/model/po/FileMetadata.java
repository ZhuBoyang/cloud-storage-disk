package online.yangcloud.model.po;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;

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
                + " ancestors=" + ancestors
                + " ]"
                + " "
                + super.toString();
    }
}
