package online.yangcloud.common.model.view;

import java.util.List;

/**
 * 分页视图数据
 *
 * @author zhuboyang
 * @since 2022/3/11 11:44
 */
public class PagerView<T> {

    /**
     * 数据总量
     */
    private Integer total;

    /**
     * 每页的数据
     */
    private List<T> data;

    public static <T> PagerView<T> initial(Integer total, List<T> data) {
        return new PagerView<T>().setTotal(total).setData(data);
    }

    public Integer getTotal() {
        return total;
    }

    public PagerView<T> setTotal(Integer total) {
        this.total = total;
        return this;
    }

    public List<T> getData() {
        return data;
    }

    public PagerView<T> setData(List<T> data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "PagerHelper["
                + " total=" + total + ","
                + " data=" + data
                + " ]";
    }

}
