package online.yangcloud.common.model;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import online.yangcloud.common.annotation.DatabaseColumn;
import online.yangcloud.common.annotation.DatabaseIndex;
import online.yangcloud.common.enumration.ColumnTypeEnum;

/**
 * @author zhuboyang
 * @since 2023年11月02 10:25:37
 */
@FluentMybatis(table = "preview_convert_task", desc = "转换任务表")
public class PreviewConvertTask extends BaseParameter {

    /**
     * 主键
     */
    @TableId
    @DatabaseColumn(primary = true, name = "id", type = ColumnTypeEnum.VARCHAR, length = 32, canNull = false)
    @DatabaseIndex(unique = true)
    private String id;

    /**
     * 文件 id
     */
    @DatabaseColumn(name = "file_id", type = ColumnTypeEnum.VARCHAR, length = 32, canNull = false, comment = "文件 id")
    @DatabaseIndex
    private String fileId;

    /**
     * 是否已完成
     */
    @DatabaseColumn(name = "is_over", type = ColumnTypeEnum.TINYINT, defaultValue = "0", comment = "是否已完成，默认 0 未完成")
    @DatabaseIndex
    private Integer isOver;

    public static PreviewConvertTask builder() {
        return new PreviewConvertTask();
    }

    public String getId() {
        return id;
    }

    public PreviewConvertTask setId(String id) {
        this.id = id;
        return this;
    }

    public String getFileId() {
        return fileId;
    }

    public PreviewConvertTask setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public Integer getIsOver() {
        return isOver;
    }

    public PreviewConvertTask setIsOver(Integer isOver) {
        this.isOver = isOver;
        return this;
    }

    @Override
    public String toString() {
        return "PreviewConvertTask["
                + " id=" + id + ","
                + " fileId=" + fileId + ","
                + " isOver=" + isOver + ","
                + " ]"
                + " "
                + super.toString();
    }
}
