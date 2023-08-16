package online.yangcloud.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.sun.management.OperatingSystemMXBean;
import online.yangcloud.common.common.AppConstants;
import online.yangcloud.common.model.business.HeadersParameters;
import online.yangcloud.common.model.view.DiskInfoView;
import online.yangcloud.common.model.view.MemoryInfoView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * @author zhuboyang
 * @since 2022/12/31 11:31
 */
public class SystemTools {

    private static final Logger logger = LoggerFactory.getLogger(SystemTools.class);

    /**
     * 网站资源根目录（自动识别）
     */
    public static String systemPath() {
        String userDir = System.getProperty("user.dir");
        userDir = userDir.substring(userDir.lastIndexOf(AppConstants.Special.SEPARATOR) + 1);
        String systemName = System.getProperty("os.name").substring(0, 3);
        StringBuilder rootPathBuilder = new StringBuilder();
        switch (systemName) {
            case "Win":
                File[] root = File.listRoots();
                rootPathBuilder.append(root[root.length - 1])
                        .append("storage")
                        .append(AppConstants.Special.SEPARATOR)
                        .append(userDir);
                break;
            case "Mac":
                String username = System.getenv().get("USER");
                rootPathBuilder.append(AppConstants.Special.SEPARATOR)
                        .append("Users")
                        .append(AppConstants.Special.SEPARATOR)
                        .append(username)
                        .append(AppConstants.Special.SEPARATOR)
                        .append("storage")
                        .append(AppConstants.Special.SEPARATOR)
                        .append(userDir);
                break;
            case "Lin":
                rootPathBuilder.append(AppConstants.Special.SEPARATOR)
                        .append("opt")
                        .append(AppConstants.Special.SEPARATOR)
                        .append("webapps")
                        .append(AppConstants.Special.SEPARATOR)
                        .append("storage")
                        .append(AppConstants.Special.SEPARATOR)
                        .append(userDir);
                break;
            default:
                break;
        }
        rootPathBuilder.append(AppConstants.Special.SEPARATOR);
        String rootPath = rootPathBuilder.toString();
        File rootFile = new File(rootPath);
        if (!rootFile.exists()) {
            boolean mkdirResult = rootFile.mkdirs();
            if (mkdirResult) {
                logger.info("The system root directory is created successfully. And root directory is ：{}", rootPath);
            } else {
                logger.info("Failed to create the system root directory. Procedure.");
            }
        }
        return rootPath;
    }

    /**
     * 获取系统各个硬盘的总容量、已经使用的容量、剩余容量和使用率
     *
     * @param drive 要查询的目录空间地址
     * @return 各使用率信息
     */
    public static DiskInfoView findDiskInfo(String drive) {
        File file = new File(drive);
        return DiskInfoView.pack(file.getTotalSpace(), file.getTotalSpace() - file.getUsableSpace(), file.getUsableSpace());
    }

    /**
     * 获取内存使用情况
     *
     * @return 各使用率信息
     */
    public static MemoryInfoView findMemoryInfo() {
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return MemoryInfoView.pack(mem.getTotalPhysicalMemorySize(), mem.getFreePhysicalMemorySize());
    }

    /**
     * 获取请求头中自定义的属性字段
     *
     * @return 自定义的属性字段
     */
    public static HeadersParameters getHeaders() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjectUtil.isNull(requestAttributes)) {
            return new HeadersParameters();
        }
        // get the request
        HttpServletRequest request = requestAttributes.getRequest();
        return new HeadersParameters().setAuthorization(request.getHeader(AppConstants.Account.AUTHORIZATION));
    }

}
