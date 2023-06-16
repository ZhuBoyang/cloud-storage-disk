package online.yangcloud.model;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import online.yangcloud.common.constants.AppConstants;
import online.yangcloud.utils.IdTools;

/**
 * 文件块元数据
 *
 * @author zhuboyang
 * @since 2023年01月01 21:52:34
 */
@FluentMybatis
public class BlockMetadata extends BaseParameter {

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
    private String path;

    /**
     * 文件块大小
     */
    private Long size;

    /**
     * 初始化文件块元数据
     *
     * @param hash      文件块 hash
     * @param blockSize 文件块大小
     * @return 文件块元数据实体
     */
    public static BlockMetadata initial(String hash, Long blockSize) {
        return new BlockMetadata()
                .setId(IdTools.fastSimpleUuid())
                .setHash(hash)
                .setPath(AppConstants.Uploader.BLOCK_UPLOAD_PATH + hash)
                .setSize(blockSize);
    }

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

    public String getPath() {
        return path;
    }

    public BlockMetadata setPath(String path) {
        this.path = path;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public BlockMetadata setSize(Long size) {
        this.size = size;
        return this;
    }

    @Override
    public String toString() {
        return "BlockMetadata["
                + " id=" + id + ","
                + " hash=" + hash + ","
                + " path=" + path + ","
                + " size=" + size + ","
                + " ]"
                + " "
                + super.toString();
    }
}
