package online.yangcloud.model.vo;

/**
 * @author zhuboyang
 * @since 2022年09月06 14:08:34
 */
public class SystemDiskInfoView {

    /**
     * 磁盘总容量
     */
    private Long total;

    /**
     * 磁盘已用容量
     */
    private Long used;

    /**
     * 磁盘剩余容量
     */
    private Long free;

    public Long getTotal() {
        return total;
    }

    public SystemDiskInfoView setTotal(Long total) {
        this.total = total;
        return this;
    }

    public Long getUsed() {
        return used;
    }

    public SystemDiskInfoView setUsed(Long used) {
        this.used = used;
        return this;
    }

    public Long getFree() {
        return free;
    }

    public SystemDiskInfoView setFree(Long free) {
        this.free = free;
        return this;
    }

    @Override
    public String toString() {
        return "SystemInfoView["
                + " diskTotal=" + total + ","
                + " diskUsed=" + used + ","
                + " diskFree=" + free
                + " ]";
    }
}
