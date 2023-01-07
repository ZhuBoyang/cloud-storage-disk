package online.yangcloud.model.ao.file;

/**
 * 检测文件块是否已入库的请求参数
 *
 * @author zhuboyang
 * @since 2023年01月06 13:23:31
 */
public class BlockCheckExistRequest {

    /**
     * 文件识别码
     */
    private String identifier;

    /**
     * 文件块 hash 列表
     */
    private String hashList;

    /**
     * 文件父级目录 fid
     */
    private String pid;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件块分片大小
     */
    private Long shardingSize;

    /**
     * 是否开启分片
     */
    private Boolean shard;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getHashList() {
        return hashList;
    }

    public void setHashList(String hashList) {
        this.hashList = hashList;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getShardingSize() {
        return shardingSize;
    }

    public void setShardingSize(Long shardingSize) {
        this.shardingSize = shardingSize;
    }

    public Boolean getShard() {
        return shard;
    }

    public void setShard(Boolean shard) {
        this.shard = shard;
    }

    @Override
    public String toString() {
        return "BlockCheckExistRequest["
                + " identifier=" + identifier + ","
                + " hashList=" + hashList + ","
                + " pid=" + pid + ","
                + " fileName=" + fileName + ","
                + " fileSize=" + fileSize + ","
                + " shardingSize=" + shardingSize + ","
                + " shard=" + shard
                + " ]";
    }
}
