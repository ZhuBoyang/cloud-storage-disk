package online.yangcloud.utils;

import com.sun.management.OperatingSystemMXBean;
import online.yangcloud.model.vo.SystemDiskInfoView;
import online.yangcloud.model.vo.SystemMemoryInfoView;

import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * @author zhuboyang
 * @since 2022/12/31 11:31
 */
public class SystemUtils {

    /**
     * 获取系统各个硬盘的总容量、已经使用的容量、剩余容量和使用率
     *
     * @param drive 要查询的目录空间地址
     */
    public static SystemDiskInfoView findDiskInfo(String drive) {
        File diskFile = new File(drive);
        return new SystemDiskInfoView()
                .setTotal(diskFile.getTotalSpace())
                .setUsed(diskFile.getTotalSpace() - diskFile.getUsableSpace())
                .setFree(diskFile.getUsableSpace());
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
