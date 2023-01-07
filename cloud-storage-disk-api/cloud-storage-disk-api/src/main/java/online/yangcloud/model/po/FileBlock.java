package online.yangcloud.model.po;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;

/**
 * @author zhuboyang
 * @since 2022年12月31 21:23:03
 */
@FluentMybatis
public class FileBlock extends RichEntity {

    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 文件块 id
     */
    private String blockId;

    /**
     * 文件 id
     */
    private String fileId;

    /**
     * 当前文件块序号
     */
    private Integer blockIndex;

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
     * 是否分片：0.不分配；1.分片
     */
    private Integer shard;

    public String getId() {
        return id;
    }

    public FileBlock setId(String id) {
        this.id = id;
        return this;
    }

    public String getBlockId() {
        return blockId;
    }

    public FileBlock setBlockId(String blockId) {
        this.blockId = blockId;
        return this;
    }

    public String getFileId() {
        return fileId;
    }

    public FileBlock setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public Integer getBlockIndex() {
        return blockIndex;
    }

    public FileBlock setBlockIndex(Integer blockIndex) {
        this.blockIndex = blockIndex;
        return this;
    }

    public Integer getBlockCount() {
        return blockCount;
    }

    public FileBlock setBlockCount(Integer blockCount) {
        this.blockCount = blockCount;
        return this;
    }

    public Long getShardingSize() {
        return shardingSize;
    }

    public FileBlock setShardingSize(Long shardingSize) {
        this.shardingSize = shardingSize;
        return this;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public FileBlock setFileSize(Long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public Integer getShard() {
        return shard;
    }

    public FileBlock setShard(Integer shard) {
        this.shard = shard;
        return this;
    }

    @Override
    public String toString() {
        return "FileBlock["
                + " id=" + id + ","
                + " blockId=" + blockId + ","
                + " fileId=" + fileId + ","
                + " blockIndex=" + blockIndex + ","
                + " blockCount=" + blockCount + ","
                + " shardingSize=" + shardingSize + ","
                + " fileSize=" + fileSize + ","
                + " shard=" + shard
                + " ]"
                + " "
                + super.toString();
    }
}
