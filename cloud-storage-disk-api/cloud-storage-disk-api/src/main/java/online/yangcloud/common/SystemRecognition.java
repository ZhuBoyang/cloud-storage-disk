package online.yangcloud.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.properties.ProjectProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

/**
 * 多平台环境下，自动识别及建立系统根目录
 *
 * @author zhuboyang
 * @since 2022/12/31 11:56
 */
@Component
public class SystemRecognition {

    private static final Logger logger = LoggerFactory.getLogger(SystemRecognition.class);

    @Autowired
    private ProjectProperties projectProperties;

    /**
     * 网站资源根目录（自动识别）
     */
    public String generateSystemPath() {
        Map<String, String> envMap = System.getenv();
        String systemName = System.getProperty("os.name").substring(0, 3);
        StringBuilder rootPathBuilder = new StringBuilder();
        switch (systemName) {
            case "Win":
                File[] root = File.listRoots();
                rootPathBuilder.append(root[root.length - 1])
                        .append(projectProperties.getPath())
                        .append(File.separator);
                break;
            case "Mac":
                String username = envMap.get("USER");
                rootPathBuilder.append(StrUtil.SLASH)
                        .append("Users")
                        .append(StrUtil.SLASH)
                        .append(username)
                        .append(StrUtil.SLASH)
                        .append(projectProperties.getPath())
                        .append(StrUtil.SLASH);
                break;
            case "Lin":
                rootPathBuilder.append(StrUtil.SLASH)
                        .append("opt")
                        .append(StrUtil.SLASH)
                        .append("webapps")
                        .append(StrUtil.SLASH)
                        .append(projectProperties.getPath())
                        .append(StrUtil.SLASH);
                break;
            default:
                break;
        }
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
     * 检测并创建文件上传目录
     *
     * @return 文件上传目录路径
     */
    public String generateUploadPath() {
        String systemPath = generateSystemPath();
        String uploadPath = systemPath + "upload";
        if (!FileUtil.exist(uploadPath)) {
            FileUtil.mkdir(uploadPath);
            logger.info("upload path is not exist, and has exist now");
        }
        return uploadPath;
    }

    /**
     * 生成文件块存储路径
     *
     * @return 文件块存储路径
     */
    public String generateBlockStoragePath() {
        String uploadPath = generateUploadPath();
        String blockPath = uploadPath + File.separator + "block";
        if (!FileUtil.exist(blockPath)) {
            FileUtil.mkdir(blockPath);
            logger.info("block storage path is not exist, and has exist now");
        }
        return blockPath;
    }

    /**
     * 生成文件存储路径
     *
     * @return 文件存储路径
     */
    public String generateFileStoragePath() {
        String uploadPath = generateUploadPath();
        String blockPath = uploadPath + File.separator + "file";
        if (!FileUtil.exist(blockPath)) {
            FileUtil.mkdir(blockPath);
            logger.info("file storage path is not exist, and has exist now");
        }
        return blockPath;
    }

}
