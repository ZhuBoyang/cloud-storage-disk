package online.yangcloud.model;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;

/**
 * 文件块元数据
 *
 * @author zhuboyang
 * @since 2023年01月01 21:52:34
 */
@FluentMybatis
public class BlockMetadata extends RichEntity {

    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 文件块 md5
     */
    private String hash;

    /**
     * 文件块存储路径
     */
    private String storagePath;

    /**
     * 文件块大小
     */
    private Long blockSize;

    /**
     * 是否已删除
     */
    private Integer isDelete;

    public String getId() {
        return id;
    }

    public BlockMetadata setId(String id) {
        this.id = id;
        return this;
    }

    public String getHash() {
        return hash;
    }

    public BlockMetadata setHash(String hash) {
        this.hash = hash;
        return this;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public BlockMetadata setStoragePath(String storagePath) {
        this.storagePath = storagePath;
        return this;
    }

    public Long getBlockSize() {
        return blockSize;
    }

    public BlockMetadata setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public BlockMetadata setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
        return this;
    }

    @Override
    public String toString() {
        return "BlockMetadata["
                + " id=" + id + ","
                + " hash=" + hash + ","
                + " storagePath=" + storagePath + ","
                + " blockSize=" + blockSize + ","
                + " isDelete=" + isDelete
                + " ]"
                + " "
                + super.toString();
    }
}
