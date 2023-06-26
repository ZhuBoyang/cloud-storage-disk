package online.yangcloud.model.ao.user;

/**
 * @author zhuboyang
 * @since 2023年06月19 16:17:47
 */
public class SpaceUpdater {

    /**
     * 空间总量
     */
    private Long total;

    /**
     * 已使用量
     */
    private Long used;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    @Override
    public String toString() {
        return "SpaceUpdater["
                + " total=" + total + ","
                + " used=" + used
                + " ]";
    }
}
