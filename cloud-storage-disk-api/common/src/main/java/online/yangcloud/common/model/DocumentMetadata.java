package online.yangcloud.common.model;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import online.yangcloud.common.annotation.DatabaseColumn;
import online.yangcloud.common.annotation.DatabaseIndex;
import online.yangcloud.common.enumration.ColumnTypeEnum;

/**
 * @author zhuboyang
 * @since 2023年10月20 11:03:52
 */
@FluentMybatis(table = "document_metadata", desc = "文档元数据")
public class DocumentMetadata extends BaseParameter {

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
     * 文档总页数
     */
    @DatabaseColumn(name = "page_total", type = ColumnTypeEnum.SMALLINT, canNull = false, comment = "文档总页数")
    @DatabaseIndex
    private Integer pageTotal;

    /**
     * 图片宽度
     */
    @DatabaseColumn(name = "width", type = ColumnTypeEnum.SMALLINT, canNull = false, comment = "图片宽度")
    private Integer width;

    /**
     * 图片高度
     */
    @DatabaseColumn(name = "height", type = ColumnTypeEnum.SMALLINT, canNull = false, comment = "图片高度")
    private Integer height;

    /**
     * 缩略图存放路径
     */
    @DatabaseColumn(name = "thumbnail", type = ColumnTypeEnum.VARCHAR, length = 1024, canNull = false, comment = "缩略图存放路径")
    private String thumbnail;

    /**
     * 解析异常类
     */
    @DatabaseColumn(name = "err_exp", type = ColumnTypeEnum.VARCHAR, length = 64, canNull = false, comment = "解析异常")
    private String errExp;

    /**
     * 解析异常信息
     */
    @DatabaseColumn(name = "err_msg", type = ColumnTypeEnum.VARCHAR, length = 1024, canNull = false, comment = "解析异常信息")
    private String errMsg;

    public static DocumentMetadata builder() {
        return new DocumentMetadata();                
    }

    public String getId() {
        return id;
    }

    public DocumentMetadata setId(String id) {
        this.id = id;
        return this;
    }

    public String getFileId() {
        return fileId;
    }

    public DocumentMetadata setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public Integer getPageTotal() {
        return pageTotal;
    }

    public DocumentMetadata setPageTotal(Integer pageTotal) {
        this.pageTotal = pageTotal;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    public DocumentMetadata setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    public DocumentMetadata setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public DocumentMetadata setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public String getErrExp() {
        return errExp;
    }

    public DocumentMetadata setErrExp(String errExp) {
        this.errExp = errExp;
        return this;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public DocumentMetadata setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }

    @Override
    public String toString() {
        return "DocumentMetadata["
                + " id=" + id + ","
                + " fileId=" + fileId + ","
                + " pageTotal=" + pageTotal + ","
                + " width=" + width + ","
                + " height=" + height + ","
                + " thumbnail=" + thumbnail + ","
                + " errExp=" + errExp + ","
                + " errMsg=" + errMsg
                + " ]"
                + " "
                + super.toString();
    }
}
