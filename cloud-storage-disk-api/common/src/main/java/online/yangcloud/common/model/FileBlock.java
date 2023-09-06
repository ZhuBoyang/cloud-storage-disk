package online.yangcloud.common.model;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import online.yangcloud.common.common.AppResultCode;
import online.yangcloud.common.model.request.file.FileUploader;
import online.yangcloud.common.tools.ExceptionTools;
import online.yangcloud.common.tools.IdTools;

/**
 * @author zhuboyang
 * @since 2022年12月31 21:23:03
 */
@FluentMybatis(table = "file_block")
public class FileBlock extends BaseParameter {

    /**
     * id
     */
    @TableId
    private String id;

    /**
     * 文件 id
     */
    private String fileId;

    /**
     * 文件块 id
     */
    private String blockId;

    /**
     * 当前文件块序号
     */
    private Integer index;

    /**
     * 文件块数量
     */
    private Integer count;

    /**
     * 文件块分片大小
     */
    private Long shardingSize;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 是否分片：0.不分片；1.分片
     */
    private Integer isShard;

    /**
     * 初始化文件与文件块之间的关联关系
     *
     * @param fileId   文件 id
     * @param uploader 上传的文件块数据
     * @param block    文件块元数据
     * @return 关联数据
     */
    public static FileBlock initial(String fileId, FileUploader uploader, BlockMetadata block) {
        if (ObjectUtil.isNull(block)) {
            ExceptionTools.businessLogger(AppResultCode.FAILURE.getMessage());
        }
        return new FileBlock()
                .setId(IdTools.fastSimpleUuid())
                .setFileId(fileId)
                .setBlockId(block.getId())
                .setFileSize(uploader.getFileSize())
                .setCount(uploader.getBlockCount())
                .setIndex(uploader.getBlockIndex())
                .setShardingSize(uploader.getShardingSize())
                .setIsShard(uploader.getShard());
    }

    public FileBlock updateId(String id) {
        this.id = id;
        this.setCreateTime(DateUtil.date().getTime());
        this.setUpdateTime(DateUtil.date().getTime());
        return this;
    }

    public String getId() {
        return id;
    }

    public FileBlock setId(String id) {
        this.id = id;
        return this;
    }

    public String getFileId() {
        return fileId;
    }

    public FileBlock setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public String getBlockId() {
        return blockId;
    }

    public FileBlock setBlockId(String blockId) {
        this.blockId = blockId;
        return this;
    }

    public Integer getIndex() {
        return index;
    }

    public FileBlock setIndex(Integer index) {
        this.index = index;
        return this;
    }

    public Integer getCount() {
        return count;
    }

    public FileBlock setCount(Integer count) {
        this.count = count;
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

    public Integer getIsShard() {
        return isShard;
    }

    public FileBlock setIsShard(Integer isShard) {
        this.isShard = isShard;
        return this;
    }

    @Override
    public String toString() {
        return "FileBlock["
                + " id=" + id + ","
                + " fileId=" + fileId + ","
                + " blockId=" + blockId + ","
                + " index=" + index + ","
                + " count=" + count + ","
                + " shardingSize=" + shardingSize + ","
                + " fileSize=" + fileSize + ","
                + " isShard=" + isShard + ","
                + " ]"
                + " "
                + super.toString();
    }
}
