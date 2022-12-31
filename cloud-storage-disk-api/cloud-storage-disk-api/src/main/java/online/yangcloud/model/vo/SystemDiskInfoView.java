package online.yangcloud.model.vo;

/**
 * @author zhuboyang
 * @since 2022年09月06 14:08:34
 */
public class SystemDiskInfoView {

    /**
     * 磁盘总容量
     */
    private String diskTotal;

    /**
     * 磁盘已用容量
     */
    private String diskUsed;

    /**
     * 磁盘剩余容量
     */
    private String diskFree;

    public String getDiskTotal() {
        return diskTotal;
    }

    public SystemDiskInfoView setDiskTotal(String diskTotal) {
        this.diskTotal = diskTotal;
        return this;
    }

    public String getDiskUsed() {
        return diskUsed;
    }

    public SystemDiskInfoView setDiskUsed(String diskUsed) {
        this.diskUsed = diskUsed;
        return this;
    }

    public String getDiskFree() {
        return diskFree;
    }

    public SystemDiskInfoView setDiskFree(String diskFree) {
        this.diskFree = diskFree;
        return this;
    }

    @Override
    public String toString() {
        return "SystemInfoView["
                + " diskTotal=" + diskTotal + ","
                + " diskUsed=" + diskUsed + ","
                + " diskFree=" + diskFree
                + " ]";
    }
}
