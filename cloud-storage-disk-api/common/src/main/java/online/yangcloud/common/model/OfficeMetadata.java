package online.yangcloud.common.model;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import online.yangcloud.common.annotation.DatabaseColumn;
import online.yangcloud.common.annotation.DatabaseIndex;
import online.yangcloud.common.enumration.DatabaseColumnTypeEnum;

/**
 * @author zhuboyang
 * @since 2023年10月20 11:03:52
 */
@FluentMybatis(table = "office_metadata", desc = "office 文档元数据")
public class OfficeMetadata extends BaseParameter {

    /**
     * 主键
     */
    @TableId
    @DatabaseColumn(primary = true, name = "id", type = DatabaseColumnTypeEnum.VARCHAR, length = 32, canNull = false)
    @DatabaseIndex(unique = true)
    private String id;

    /**
     * 文件 id
     */
    @DatabaseColumn(name = "file_id", type = DatabaseColumnTypeEnum.VARCHAR, length = 32, canNull = false, comment = "文件 id")
    @DatabaseIndex
    private String fileId;

    /**
     * 文档总页数
     */
    @DatabaseColumn(name = "page_total", type = DatabaseColumnTypeEnum.SMALLINT, canNull = false, comment = "文档总页数")
    @DatabaseIndex
    private Integer pageTotal;

    /**
     * 图片宽度
     */
    @DatabaseColumn(name = "width", type = DatabaseColumnTypeEnum.SMALLINT, canNull = false, comment = "图片宽度")
    private Integer width;

    /**
     * 图片高度
     */
    @DatabaseColumn(name = "height", type = DatabaseColumnTypeEnum.SMALLINT, canNull = false, comment = "图片高度")
    private Integer height;

    /**
     * 缩略图存放路径
     */
    @DatabaseColumn(name = "thumbnail", type = DatabaseColumnTypeEnum.VARCHAR, length = 1024, canNull = false, comment = "缩略图存放路径")
    private String thumbnail;

    public static OfficeMetadata builder() {
        return new OfficeMetadata();                
    }

    public String getId() {
        return id;
    }

    public OfficeMetadata setId(String id) {
        this.id = id;
        return this;
    }

    public String getFileId() {
        return fileId;
    }

    public OfficeMetadata setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public Integer getPageTotal() {
        return pageTotal;
    }

    public OfficeMetadata setPageTotal(Integer pageTotal) {
        this.pageTotal = pageTotal;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    public OfficeMetadata setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    public OfficeMetadata setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public OfficeMetadata setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    @Override
    public String toString() {
        return "OfficeMetadata["
                + " id=" + id + ","
                + " fileId=" + fileId + ","
                + " pageTotal=" + pageTotal + ","
                + " width=" + width + ","
                + " height=" + height + ","
                + " thumbnail=" + thumbnail
                + " ]"
                + " "
                + super.toString();
    }
}
