package online.yangcloud.utils;

import cn.hutool.core.io.FileUtil;
import online.yangcloud.enumration.FileCategoryEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * 文件工具类
 *
 * @author zhuboyang
 * @since 2023年01月03 13:39:25
 */
public class FileTools {

    private static final Logger logger = LoggerFactory.getLogger(FileTools.class);

    private final static int BUF_SIZE = 1024;
    private final static String PREFIX = "data:image/jpeg;base64,";
    public final static String[] EXT = {".bmp", ".jpg", ".jpeg", ".png", ".gif"};

    /**
     * 文件合并
     *
     * @param targetPath 合并后目标文件
     * @param blocks     文件块路径列表
     */
    public static void merge(String targetPath, List<String> blocks) {
        // 创建源
        File target = new File(targetPath);
        // 创建一个容器
        Vector<InputStream> vi = new Vector<>();
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target, true));
             SequenceInputStream sis = new SequenceInputStream(vi.elements())) {
            for (String block : blocks) {
                vi.add(new BufferedInputStream(Files.newInputStream(new File(block).toPath())));
            }
            // 缓冲区
            byte[] flush = new byte[1024];
            // 接收长度
            int len;
            while (-1 != (len = sis.read(flush))) {
                bos.write(flush, 0, len);
            }
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            byte[] buffer = new byte[1024 * 1024 * 2];
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
     * 判断文件类型是否为指定类型
     *
     * @param category 指定的文件类型
     * @param ext      文件后缀
     * @return result
     */
    public static boolean determineFileType(FileCategoryEnum category, String ext) {
        if (FileCategoryEnum.VIDEO.equals(category)) {
            List<String> categories = Arrays.asList("avi", "mov", "rmvb", "rm", "flv", "mp4", "3gp");
            return categories.contains(ext);
        }
        return Boolean.FALSE;
    }

    /**
     * 下载文档
     *
     * @param sourceName   要下载的文件
     * @param downloadName 下载文件的名称
     * @param response     响应
     */
    public static void downloadResponse(String sourceName, String downloadName, HttpServletResponse response) {
        byte[] buff = new byte[1024];
        OutputStream os = null;
        BufferedInputStream bis = null;
        try {
            // 取得输出流
            os = response.getOutputStream();
            // 清空输出流
            response.reset();
            response.setContentType("application/x-download;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="
                    + new String(downloadName.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));

            bis = new BufferedInputStream(Files.newInputStream(Paths.get(sourceName)));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            logger.info("file read or write error {}", e.getMessage(), e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    logger.info("input stream close error {}", e.getMessage(), e);
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    logger.info("output stream close error {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 校验文件是否是图片
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public static boolean isPic(String ext) {
        return Arrays.asList(EXT).contains(ext);
    }

}
