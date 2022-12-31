package online.yangcloud.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.constants.FileConstants;
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
 * @since 2022/3/11 09:40
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
        String uploadPath = systemPath + FileConstants.FILE_BLOCK_UPLOAD_DIR;
        if (!FileUtil.exist(uploadPath)) {
            FileUtil.mkdir(uploadPath);
            logger.info("upload path is not exist, and has exist now");
        }
        return uploadPath;
    }

    /**
     * 获取文件块存放的目录
     *
     * @return 文件块存放的目录
     * @since 2021/9/3 21:38
     */
    public String fileChunkPath() {
        String fileChunkPath = generateSystemPath() + "chunk" + File.separator;
        File chunkFile = new File(fileChunkPath);
        if (!FileUtil.exist(fileChunkPath)) {
            logger.info("The system upload directory does not exist. Perform initialization");
            FileUtil.mkdir(chunkFile);
        }
        return fileChunkPath;
    }

    /**
     * 获取缩略图存放的目录
     *
     * @return result
     */
    public String thumbnailPath() {
        String fileThumbnailPath = generateSystemPath() + "thumbnail" + File.separator;
        if (!FileUtil.exist(fileThumbnailPath)) {
            logger.info("The system thumbnail directory does not exist. Perform initialization");
            FileUtil.mkdir(fileThumbnailPath);
        }
        return fileThumbnailPath;
    }

}
