package online.yangcloud.model.ao.file;

/**
 * 文件夹面包屑请求
 *
 * @author zhuboyang
 * @since 2023年01月12 17:28:38
 */
public class DirBreadsQueryRequest {

    /**
     * 父级目录 id
     */
    private String parentId;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "DirBreadsQueryRequest["
                + " parentId=" + parentId
                + " ]";
    }
}
