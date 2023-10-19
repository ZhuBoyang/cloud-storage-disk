package online.yangcloud.common.model;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import online.yangcloud.common.annotation.DatabaseColumn;
import online.yangcloud.common.annotation.DatabaseIndex;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.enumration.DatabaseColumnTypeEnum;
import online.yangcloud.common.enumration.FileTypeEnum;
import online.yangcloud.common.model.request.file.FileUploader;
import online.yangcloud.common.tools.IdTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author zhuboyang
 * @since 2022年12月31 21:20:20
 */
@FluentMybatis(table = "file_metadata", desc = "文件元数据")
public class FileMetadata extends BaseParameter {

    /**
     * id
     */
    @TableId
    @DatabaseColumn(primary = true, name = "id", type = DatabaseColumnTypeEnum.VARCHAR, length = 32, canNull = false, comment = "文件 id")
    @DatabaseIndex(unique = true)
    private String id;

    /**
     * 父级目录 id
     */
    @DatabaseColumn(name = "pid", type = DatabaseColumnTypeEnum.VARCHAR, length = 32, canNull = false, comment = "上级文件 id")
    private String pid;

    /**
     * 文件名
     */
    @DatabaseColumn(name = "name", type = DatabaseColumnTypeEnum.VARCHAR, length = 1000, canNull = false, comment = "文件名")
    private String name;

    /**
     * 文件存储路径
     */
    @DatabaseColumn(name = "path", type = DatabaseColumnTypeEnum.VARCHAR, length = 1000, canNull = false, comment = "文件存储路径")
    private String path;

    /**
     * 文件 hash
     */
    @DatabaseColumn(name = "hash", type = DatabaseColumnTypeEnum.VARCHAR, length = 32, canNull = false, comment = "文件 hash")
    @DatabaseIndex
    private String hash;

    /**
     * 文件类型：0.文件；1.文件夹
     */
    @DatabaseColumn(name = "type", type = DatabaseColumnTypeEnum.TINYINT, canNull = false, comment = "文件类型：0.文件；1.文件夹")
    private Integer type;

    /**
     * 文件后缀
     */
    @DatabaseColumn(name = "ext", type = DatabaseColumnTypeEnum.VARCHAR, length = 10, canNull = false, comment = "文件后缀名")
    private String ext;

    /**
     * 文件大小
     */
    @DatabaseColumn(name = "size", type = DatabaseColumnTypeEnum.BIGINT, canNull = false, comment = "文件大小")
    private Long size;

    /**
     * 上传时间
     */
    @DatabaseColumn(name = "upload_time", type = DatabaseColumnTypeEnum.BIGINT, canNull = false, comment = "上传时间")
    private Long uploadTime;

    /**
     * 所有父级目录 id
     */
    @DatabaseColumn(name = "ancestors", type = DatabaseColumnTypeEnum.TEXT, canNull = false, comment = "祖级文件 id 列表")
    private String ancestors;

    /**
     * 用户 id
     */
    @DatabaseColumn(name = "user_id", type = DatabaseColumnTypeEnum.VARCHAR, length = 32, canNull = false, comment = "文件操作人 id")
    private String userId;

    public static FileMetadata initial(String userId) {
        return new FileMetadata()
                .setId(IdTools.fastSimpleUuid())
                .setPid(CharSequenceUtil.EMPTY)
                .setName("全部")
                .setHash(CharSequenceUtil.EMPTY)
                .setExt(CharSequenceUtil.EMPTY)
                .setPath(CharSequenceUtil.EMPTY)
                .setType(FileTypeEnum.DIR.code())
                .setSize(0L)
                .setAncestors(CharSequenceUtil.EMPTY)
                .setUploadTime(DateUtil.date().getTime())
                .setUserId(userId);
    }

    public static FileMetadata initial(String fileId, String name, String fileHash, FileMetadata parent,
                                       FileUploader uploader, User user) {
        List<String> ancestors = parent.queryAncestors();
        ancestors.add(parent.getId());
        return new FileMetadata()
                .setId(fileId)
                .setPid(uploader.getId())
                .setName(name)
                .setHash(fileHash)
                .setExt(uploader.getExt())
                .setPath(AppConstants.Uploader.FILE + fileHash)
                .setType(FileTypeEnum.FILE.code())
                .setSize(uploader.getFileSize())
                .setterAncestors(ancestors)
                .setUploadTime(DateUtil.date().getTime())
                .setUserId(user.getId());
    }

    public static FileMetadata initialDir(String pid, String name, List<String> ancestors, String userId) {
        ancestors.add(pid);
        return new FileMetadata()
                .setId(IdTools.fastSimpleUuid())
                .setPid(pid)
                .setName(name)
                .setHash(CharSequenceUtil.EMPTY)
                .setExt(CharSequenceUtil.EMPTY)
                .setPath(CharSequenceUtil.EMPTY)
                .setType(FileTypeEnum.DIR.code())
                .setSize(0L)
                .setterAncestors(ancestors)
                .setUploadTime(DateUtil.date().getTime())
                .setUserId(userId);
    }

    public static FileMetadata packNew(FileMetadata file, String newPid, List<String> ancestors) {
        FileMetadata o = new FileMetadata()
                .setId(IdTools.fastSimpleUuid())
                .setPid(newPid)
                .setName(file.getName())
                .setHash(file.getHash())
                .setExt(file.getExt())
                .setPath(file.getPath())
                .setType(file.getType())
                .setSize(file.getSize())
                .setterAncestors(ancestors)
                .setUploadTime(DateUtil.date().getTime())
                .setUserId(file.getUserId());
        o.setCreateTime(DateUtil.date().getTime());
        o.setUpdateTime(DateUtil.date().getTime());
        return o;
    }

    public static int calculateFileSuffixNumber(List<FileMetadata> files, String fileName) {
        int fileNumber = 0;
        List<Integer> numbers = new ArrayList<>();
        if (!files.isEmpty()) {
            for (FileMetadata file : files) {
                String name = file.getName();
                if (name.equals(fileName)) {
                    numbers.add(0);
                }
                if (name.contains(fileName) && name.indexOf(fileName) == 0) {
                    name = name.replace(fileName, CharSequenceUtil.EMPTY);
                    if (name.length() < 3) {
                        continue;
                    }
                    name = name.substring(1, name.length() - 1);
                    if (NumberUtil.isInteger(name)) {
                        numbers.add(Integer.parseInt(name));
                    }
                }
            }
        }
        Collections.sort(numbers);
        return numbers.isEmpty() ? fileNumber : numbers.get(numbers.size() - 1) + 1;
    }

    public List<String> queryAncestors() {
        return CharSequenceUtil.split(ancestors, StrUtil.UNDERLINE);
    }

    public List<String> addIdInAncestors() {
        List<String> ancestors = CharSequenceUtil.split(this.ancestors, StrUtil.UNDERLINE);
        ancestors.add(this.id);
        return ancestors;
    }

    public FileMetadata setterAncestors(List<String> ancestors) {
        StringBuilder sbr = new StringBuilder();
        for (int i = 0; i < ancestors.size(); i++) {
            sbr.append(ancestors.get(i));
            if (i != ancestors.size() - 1) {
                sbr.append(StrUtil.UNDERLINE);
            }
        }
        this.ancestors = sbr.toString();
        return this;
    }

    public static Object[] getFields(List<FileMetadata> files, Function<FileMetadata, Object> getField) {
        return files.stream().map(getField).toArray(Object[]::new);
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

    public Long getUploadTime() {
        return uploadTime;
    }

    public FileMetadata setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
        return this;
    }

    public String getAncestors() {
        return ancestors;
    }

    public FileMetadata setAncestors(String ancestors) {
        this.ancestors = ancestors;
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
                + " ancestors=" + ancestors + ","
                + " userId=" + userId + ","
                + " ]"
                + " "
                + super.toString();
    }
}
