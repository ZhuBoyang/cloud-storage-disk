package online.yangcloud.model.vo.file;

import java.util.Date;

/**
 * 文件元数据视图模型
 *
 * @author zhuboyang
 * @since 2023年01月03 11:14:53
 */
public class FileMetadataView {

    /**
     * 文件 id
     */
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

    public String getId() {
        return id;
    }

    public FileMetadataView setId(String id) {
        this.id = id;
        return this;
    }

    public String getPid() {
        return pid;
    }

    public FileMetadataView setPid(String pid) {
        this.pid = pid;
        return this;
    }

    public String getName() {
        return name;
    }

    public FileMetadataView setName(String name) {
        this.name = name;
        return this;
    }

    public String getPath() {
        return path;
    }

    public FileMetadataView setPath(String path) {
        this.path = path;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public FileMetadataView setType(Integer type) {
        this.type = type;
        return this;
    }

    public String getExt() {
        return ext;
    }

    public FileMetadataView setExt(String ext) {
        this.ext = ext;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public FileMetadataView setSize(Long size) {
        this.size = size;
        return this;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public FileMetadataView setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public FileMetadataView setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public String toString() {
        return "FileMetadataView["
                + " id=" + id + ","
                + " pid=" + pid + ","
                + " name=" + name + ","
                + " path=" + path + ","
                + " type=" + type + ","
                + " ext=" + ext + ","
                + " size=" + size + ","
                + " uploadTime=" + uploadTime + ","
                + " updateTime=" + updateTime
                + " ]";
    }
}
