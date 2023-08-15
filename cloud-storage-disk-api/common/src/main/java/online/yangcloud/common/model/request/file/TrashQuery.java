package online.yangcloud.common.model.request.file;

/**
 * @author zhuboyang
 * @since 2023年07月20 20:23:12
 */
public class TrashQuery {

    /**
     * 页码
     */
    private Integer pageIndex;

    /**
     * 每次请求的数据量
     */
    private Integer pageSize;

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "TrashQuery["
                + " pageIndex=" + pageIndex + ","
                + " pageSize=" + pageSize
                + " ]";
    }
}
