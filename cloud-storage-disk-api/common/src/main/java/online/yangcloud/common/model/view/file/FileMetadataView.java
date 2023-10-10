package online.yangcloud.common.model.view.file;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.model.FileMetadata;

/**
 * @author zhuboyang
 * @since 2023年06月04 16:59:13
 */
public class FileMetadataView {

    /**
     * id
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
    private Long uploadTime;

    /**
     * 用户 id
     */
    private String userId;

    /**
     * 扩展信息
     */
    private JSONObject extend = JSONUtil.createObj();

    public static FileMetadataView convert(FileMetadata file) {
        return new FileMetadataView()
                .setId(file.getId())
                .setPid(file.getPid())
                .setName(file.getName())
                .setPath(file.getPath())
                .setHash(file.getHash())
                .setType(file.getType())
                .setExt(file.getExt())
                .setSize(file.getSize())
                .setUploadTime(file.getUploadTime())
                .setUserId(file.getUserId());
    }

    public static FileMetadataView convert(FileMetadata file, Object extend) {
        return convert(file).setExtend(ObjectUtil.isNull(extend) ? JSONUtil.createObj() : JSONUtil.parseObj(extend));
    }

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

    public String getHash() {
        return hash;
    }

    public FileMetadataView setHash(String hash) {
        this.hash = hash;
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

    public Long getUploadTime() {
        return uploadTime;
    }

    public FileMetadataView setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public FileMetadataView setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public JSONObject getExtend() {
        return extend;
    }

    public FileMetadataView setExtend(JSONObject extend) {
        this.extend = extend;
        return this;
    }

    @Override
    public String toString() {
        return "FileMetadataView["
                + " id=" + id + ","
                + " pid=" + pid + ","
                + " name=" + name + ","
                + " path=" + path + ","
                + " hash=" + hash + ","
                + " type=" + type + ","
                + " ext=" + ext + ","
                + " size=" + size + ","
                + " uploadTime=" + uploadTime + ","
                + " userId=" + userId + ","
                + " extend=" + extend
                + " ]";
    }
}
