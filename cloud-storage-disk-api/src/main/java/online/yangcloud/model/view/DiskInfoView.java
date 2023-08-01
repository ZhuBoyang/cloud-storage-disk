package online.yangcloud.model.view;

/**
 * @author zhuboyang
 * @since 2022年09月06 14:08:34
 */
public class DiskInfoView {

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

    public static DiskInfoView pack(Long total, Long used, Long free) {
        return new DiskInfoView()
                .setTotal(total)
                .setUsed(used)
                .setFree(free);
    }

    public Long getTotal() {
        return total;
    }

    public DiskInfoView setTotal(Long total) {
        this.total = total;
        return this;
    }

    public Long getUsed() {
        return used;
    }

    public DiskInfoView setUsed(Long used) {
        this.used = used;
        return this;
    }

    public Long getFree() {
        return free;
    }

    public DiskInfoView setFree(Long free) {
        this.free = free;
        return this;
    }

    @Override
    public String toString() {
        return "DiskInfoView["
                + " diskTotal=" + total + ","
                + " diskUsed=" + used + ","
                + " diskFree=" + free
                + " ]";
    }
}
