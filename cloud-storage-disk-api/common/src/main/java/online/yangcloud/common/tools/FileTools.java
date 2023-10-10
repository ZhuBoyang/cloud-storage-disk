package online.yangcloud.common.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import online.yangcloud.common.properties.FileExtTypeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 文件工具类
 *
 * @author zhuboyang
 * @since 2023年01月03 13:39:25
 */
@Component
public class FileTools {

    private static final Logger logger = LoggerFactory.getLogger(FileTools.class);

    private final static int BUF_SIZE = 1024;
    private final static String PREFIX = "data:image/jpeg;base64,";

    @Resource
    private FileExtTypeProperty fileExtTypeProperty;

    /**
     * 图片后缀
     */
    public final static List<String> PICTURE_EXT = new ArrayList<>(Arrays.asList(".bmp", ".jpg", ".jpeg", ".png", ".gif"));

    /**
     * 文件合并
     *
     * @param target  合并后目标文件
     * @param sources 文件块路径列表
     */
    public static void combineFile(String target, List<String> sources) {
        FileUtil.mkParentDirs(target);
        try (FileOutputStream fos = new FileOutputStream(target)) {
            FileInputStream fis;
            // 一次读取 2M 数据，将读取到的数据保存到 byte 字节数组中
            byte[] buffer = new byte[BUF_SIZE * BUF_SIZE * 2];
            int len;
            for (String fileName : sources) {
                fis = new FileInputStream(fileName);
                while ((len = fis.read(buffer)) != -1) {
                    // buffer 从指定字节数组写入。buffer: 数据中的起始偏移量， len: 写入的字数。
                    fos.write(buffer, 0, len);
                }
                fis.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            System.out.println("合并完成！");
        }
    }

    /**
     * 下载文档
     *
     * @param sourceName   要下载的文件
     * @param downloadName 下载文件的名称
     * @param response     响应
     */
    public static void downloadResponse(String sourceName, String downloadName, HttpServletResponse response) {
        byte[] buff = new byte[BUF_SIZE];
        try (OutputStream os = response.getOutputStream();
             BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(Paths.get(sourceName)))) {
            // 取得输出流
            // 清空输出流
            response.reset();
            response.setContentType("application/x-download;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="
                    + new String(downloadName.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));

            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            logger.info("file read or write error {}", e.getMessage(), e);
        }
    }

    /**
     * 计算指定目录或文件的空间占用大小
     *
     * @param root 文件或目录
     * @return 空间占用大小 / 字节
     */
    public static long calculateDirSpace(File root) {
        Queue<File> files = new ArrayDeque<>(Collections.singletonList(root));
        long space = 0;
        while (!files.isEmpty()) {
            File file = files.poll();
            space += file.length();
            if (file.isDirectory()) {
                File[] child = file.listFiles();
                if (ObjectUtil.isNotNull(child) && child.length > 0) {
                    files.addAll(Arrays.asList(child));
                }
            }
        }
        return space;
    }

    /**
     * 计算指定目录或文件的空间占用大小
     *
     * @param root 文件或目录
     * @return 空间占用大小 / 字节
     */
    public static long calculateDirSpace(String root) {
        return calculateDirSpace(FileUtil.file(root));
    }

    public FileExtTypeProperty acquireFileExtProperty() {
        return fileExtTypeProperty;
    }

    /**
     * 校验文件是否是视频
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public boolean isVideo(String ext) {
        return acquireFileExtProperty().acquireVideoSupports().contains(ext);
    }

    /**
     * 校验文件是否是音频
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public boolean isAudio(String ext) {
        return acquireFileExtProperty().acquireAudioSupports().contains(ext);
    }

    /**
     * 校验文件是否是图片
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public static boolean isPic(String ext) {
        return PICTURE_EXT.contains(ext);
    }

}
