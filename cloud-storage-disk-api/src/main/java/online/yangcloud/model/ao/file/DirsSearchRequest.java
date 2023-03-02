package online.yangcloud.model.ao.file;

/**
 * 目录下所有文件夹的查询请求
 *
 * @author zhuboyang
 * @since 2023年01月12 17:09:00
 */
public class DirsSearchRequest {

    /**
     * 目录 id
     */
    private String parentId;

    /**
     * 查询的数据量
     */
    private Integer size;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "DirsSearchRequest["
                + " parentId=" + parentId + ","
                + " size=" + size
                + " ]";
    }
}
