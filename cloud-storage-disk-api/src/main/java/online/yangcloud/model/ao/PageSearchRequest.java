package online.yangcloud.model.ao;

/**
 * @author zhuboyang
 * @since 2023年01月03 11:09:52
 */
public class PageSearchRequest {

    /**
     * 页码
     */
    private Integer pageIndex;

    /**
     * 每页显示的数据量
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
        return "PageSearchRequest["
                + " pageIndex=" + pageIndex + ","
                + " pageSize=" + pageSize
                + " ]";
    }

}
