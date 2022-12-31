package online.yangcloud.utils;

import com.sun.management.OperatingSystemMXBean;
import online.yangcloud.model.vo.SystemDiskInfoView;
import online.yangcloud.model.vo.SystemMemoryInfoView;

import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * @author zhuboyang
 * @since 2022年09月06 12:59:12
 */
public class RainSystemUtil {

    /**
     * 获取系统各个硬盘的总容量、已经使用的容量、剩余容量和使用率
     *
     * @param drive 要查询的目录空间地址
     */
    public static SystemDiskInfoView findDiskInfo(String drive) {
        File diskFile = new File(drive);
        return new SystemDiskInfoView()
                .setDiskTotal(RainByteUtil.convertFileSize(diskFile.getTotalSpace()))
                .setDiskUsed(RainByteUtil.convertFileSize(diskFile.getTotalSpace() - diskFile.getUsableSpace()))
                .setDiskFree(RainByteUtil.convertFileSize(diskFile.getUsableSpace()));
    }

    /**
     * 获取内存使用情况
     */
    public static SystemMemoryInfoView findMemoryInfo() {
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return new SystemMemoryInfoView()
                .setTotalMemory(RainByteUtil.convertFileSize(mem.getTotalPhysicalMemorySize()))
                .setFreeMemory(RainByteUtil.convertFileSize(mem.getFreePhysicalMemorySize()));
    }

}
