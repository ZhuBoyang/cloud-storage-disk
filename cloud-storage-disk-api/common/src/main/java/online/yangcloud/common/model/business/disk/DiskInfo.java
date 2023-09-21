package online.yangcloud.common.model.business.disk;

import cn.hutool.core.util.StrUtil;

/**
 * 磁盘信息
 *
 * @author zhuboyang
 * @since 2023年09月21 13:22:15
 */
public class DiskInfo {

    /**
     * 磁盘空间盘符
     */
    private String canonicalPath = StrUtil.EMPTY;

    /**
     * 总容量
     */
    private Long totalSpace = 0L;

    /**
     * 已用容量
     */
    private Long usedSpace = 0L;

    /**
     * 磁盘剩余容量
     */
    private Long usableSpace = 0L;

    /**
     * 磁盘使用率
     */
    private Double useRate = 0.00D;

    private DiskInfo() {
    }

    public static DiskInfo builder() {
        return new DiskInfo();
    }

    public String getCanonicalPath() {
        return canonicalPath;
    }

    public DiskInfo setCanonicalPath(String canonicalPath) {
        this.canonicalPath = canonicalPath;
        return this;
    }

    public Long getTotalSpace() {
        return totalSpace;
    }

    public DiskInfo setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
        return this;
    }

    public Long getUsedSpace() {
        return usedSpace;
    }

    public DiskInfo setUsedSpace(Long usedSpace) {
        this.usedSpace = usedSpace;
        return this;
    }

    public Long getUsableSpace() {
        return usableSpace;
    }

    public DiskInfo setUsableSpace(Long usableSpace) {
        this.usableSpace = usableSpace;
        return this;
    }

    public Double getUseRate() {
        return useRate;
    }

    public DiskInfo setUseRate(Double useRate) {
        this.useRate = useRate;
        return this;
    }

    @Override
    public String toString() {
        return "DiskInfo["
                + " canonicalPath=" + canonicalPath + ","
                + " totalSpace=" + totalSpace + ","
                + " usedSpace=" + usedSpace + ","
                + " usableSpace=" + usableSpace + ","
                + " useRate=" + useRate
                + " ]";
    }
}
