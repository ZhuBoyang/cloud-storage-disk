package online.yangcloud.utils;

import java.util.List;

/**
 * 分页工具类
 *
 * @author zhuboyang
 * @since 2022/3/11 11:44
 */
public class PagerHelper<T> {

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页显示的数量
     */
    private Integer size;

    /**
     * 数据总量
     */
    private Integer total;

    /**
     * 每页的数据
     */
    private List<T> data;

    public Integer getPage() {
        return page;
    }

    public PagerHelper<T> setPage(Integer page) {
        this.page = page;
        return this;
    }

    public Integer getSize() {
        return size;
    }

    public PagerHelper<T> setSize(Integer size) {
        if (size < 1) {
            size = 20;
        }
        this.size = size;
        return this;
    }

    public Integer getTotal() {
        return total;
    }

    public PagerHelper<T> setTotal(Integer total) {
        this.total = total;
        return this;
    }

    public List<T> getData() {
        return data;
    }

    public PagerHelper<T> setData(List<T> data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "PagerHelper["
                + " page=" + page + ","
                + " size=" + size + ","
                + " total=" + total + ","
                + " data=" + data
                + " ]";
    }

}
