package online.yangcloud.model.vo;

/**
 * @author zhuboyang
 * @since 2022/12/31 10:14
 */
public class SystemMemoryInfoView {

    /**
     * 内存总量
     */
    private String totalMemory;

    /**
     * 空闲内存
     */
    private String freeMemory;

    public String getTotalMemory() {
        return totalMemory;
    }

    public SystemMemoryInfoView setTotalMemory(String totalMemory) {
        this.totalMemory = totalMemory;
        return this;
    }

    public String getFreeMemory() {
        return freeMemory;
    }

    public SystemMemoryInfoView setFreeMemory(String freeMemory) {
        this.freeMemory = freeMemory;
        return this;
    }

    @Override
    public String toString() {
        return "SystemMemoryInfoView["
                + " totalMemory=" + totalMemory + ","
                + " freeMemory=" + freeMemory
                + " ]";
    }
}
