package online.yangcloud.common.model.request;

/**
 * @author zhuboyang
 * @since 2023年03月15 16:24:20
 */
public class PagerParameter {

    /**
     * 当前页码
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
        return "PagerHelper["
                + " pageIndex=" + pageIndex + ","
                + " pageSize=" + pageSize
                + " ]";
    }
}
