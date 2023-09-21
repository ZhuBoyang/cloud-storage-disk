package online.yangcloud.common.tools;

import online.yangcloud.common.model.business.disk.DiskInfo;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * @author zhuboyang
 * @since 2023年09月21 13:21:47
 */
public class DiskTools {

    public static DiskInfo acquireDiskInfo() throws IOException {
        File[] disks = File.listRoots();
        // Mac 或 Linux 系统的磁盘
        // 由于这两种类型的系统的磁盘只有一个根目录，所以只需要考虑根目录下的磁盘空间即可
        File root = disks[0];
        // Windows 系统的磁盘，根目录盘符会是 C: 或 D: 这样的，但也需要考虑 Windows 只有一个盘符的情况
        for (File disk : disks) {
            if (disk.getCanonicalPath().toLowerCase().startsWith(SystemTools.systemPath().substring(0, 1).toLowerCase())) {
                root = disk;
            }
        }
        // 总容量
        long totalSpace = root.getTotalSpace();
        // 可用容量
        long usableSpace = root.getUsableSpace();
        // 已用容量
        long usedSpace = totalSpace - usableSpace;
        return DiskInfo.builder()
                .setCanonicalPath(root.getCanonicalPath())
                .setTotalSpace(totalSpace)
                .setUsedSpace(usedSpace)
                .setUsableSpace(usableSpace)
                .setUseRate(Double.parseDouble(new DecimalFormat("#0.00").format(usableSpace / totalSpace * 100)));
    }
    
}
