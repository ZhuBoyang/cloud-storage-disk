package online.yangcloud.model.view;

import online.yangcloud.utils.ByteTools;

/**
 * @author zhuboyang
 * @since 2022年09月06 14:18:21
 */
public class MemoryInfoView {

    /**
     * 内存总量
     */
    private String totalMemory;

    /**
     * 空闲内存
     */
    private String freeMemory;

    public static MemoryInfoView pack(Long totalMemory, Long freeMemory) {
        return new MemoryInfoView()
                .setTotalMemory(ByteTools.convertFileSize(totalMemory))
                .setFreeMemory(ByteTools.convertFileSize(freeMemory));
    }

    public String getTotalMemory() {
        return totalMemory;
    }

    public MemoryInfoView setTotalMemory(String totalMemory) {
        this.totalMemory = totalMemory;
        return this;
    }

    public String getFreeMemory() {
        return freeMemory;
    }

    public MemoryInfoView setFreeMemory(String freeMemory) {
        this.freeMemory = freeMemory;
        return this;
    }

    @Override
    public String toString() {
        return "MemoryInfoView["
                + " totalMemory=" + totalMemory + ","
                + " freeMemory=" + freeMemory
                + " ]";
    }
}
