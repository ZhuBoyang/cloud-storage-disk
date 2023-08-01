package online.yangcloud.model.request.file;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhuboyang
 * @since 2023年06月03 18:29:41
 */
public class FileUploader {

    /**
     * 上传的文件流
     */
    private MultipartFile file;

    /**
     * 当前文件块次序
     */
    private Integer blockIndex;

    /**
     * 当前文件块大小
     */
    private Long blockSize;

    /**
     * 文件块数量
     */
    private Integer blockCount;

    /**
     * 文件块分片大小
     */
    private Long shardingSize;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件识别码
     */
    private String identifier;

    /**
     * 文件名（不带后缀）
     */
    private String fileName;

    /**
     * 后缀
     */
    private String ext;

    /**
     * 文件父级目录 id
     */
    private String id;

    /**
     * 是否开启分片
     */
    private Integer shard;

    /**
     * 文件 hash
     */
    private String hash;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Integer getBlockIndex() {
        return blockIndex;
    }

    public void setBlockIndex(Integer blockIndex) {
        this.blockIndex = blockIndex;
    }

    public Long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }

    public Integer getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(Integer blockCount) {
        this.blockCount = blockCount;
    }

    public Long getShardingSize() {
        return shardingSize;
    }

    public void setShardingSize(Long shardingSize) {
        this.shardingSize = shardingSize;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getShard() {
        return shard;
    }

    public void setShard(Integer shard) {
        this.shard = shard;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "FileUploader["
                + " file=" + file + ","
                + " blockIndex=" + blockIndex + ","
                + " blockSize=" + blockSize + ","
                + " blockCount=" + blockCount + ","
                + " shardingSize=" + shardingSize + ","
                + " fileSize=" + fileSize + ","
                + " identifier=" + identifier + ","
                + " fileName=" + fileName + ","
                + " ext=" + ext + ","
                + " id=" + id + ","
                + " shard=" + shard + ","
                + " hash=" + hash
                + " ]";
    }
}
