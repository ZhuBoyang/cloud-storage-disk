package online.yangcloud.model.ao;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件块上传参数实体
 *
 * @author zhuboyang
 * @since 2022/12/31 12:08
 */
public class BlockUpload {

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
     * 文件 hash
     */
    private String hash;

    /**
     * 文件识别码
     */
    private String identifier;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件父级目录 fid
     */
    private String pid;

    /**
     * 是否开启分片
     */
    private Boolean shard;

    public MultipartFile getFile() {
        return file;
    }

    public BlockUpload setFile(MultipartFile file) {
        this.file = file;
        return this;
    }

    public Integer getBlockIndex() {
        return blockIndex;
    }

    public BlockUpload setBlockIndex(Integer blockIndex) {
        this.blockIndex = blockIndex;
        return this;
    }

    public Long getBlockSize() {
        return blockSize;
    }

    public BlockUpload setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    public Integer getBlockCount() {
        return blockCount;
    }

    public BlockUpload setBlockCount(Integer blockCount) {
        this.blockCount = blockCount;
        return this;
    }

    public Long getShardingSize() {
        return shardingSize;
    }

    public BlockUpload setShardingSize(Long shardingSize) {
        this.shardingSize = shardingSize;
        return this;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public BlockUpload setFileSize(Long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public BlockUpload setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public BlockUpload setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public BlockUpload setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getPid() {
        return pid;
    }

    public BlockUpload setPid(String pid) {
        this.pid = pid;
        return this;
    }

    public Boolean getShard() {
        return shard;
    }

    public BlockUpload setShard(Boolean shard) {
        this.shard = shard;
        return this;
    }

    @Override
    public String toString() {
        return "BlockUpload["
                + " file=" + file + ","
                + " chunkIndex=" + blockIndex + ","
                + " chunkSize=" + blockSize + ","
                + " chunkCount=" + blockCount + ","
                + " shardingSize=" + shardingSize + ","
                + " fileSize=" + fileSize + ","
                + " hash=" + hash + ","
                + " identifier=" + identifier + ","
                + " filename=" + fileName + ","
                + " pid=" + pid + ","
                + " shard=" + shard
                + " ]";
    }
}
