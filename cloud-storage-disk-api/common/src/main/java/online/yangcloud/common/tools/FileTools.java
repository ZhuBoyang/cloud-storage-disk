package online.yangcloud.common.tools;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import online.yangcloud.common.enumration.FileExtTypeEnum;
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
     * 校验文件是否是视频
     *
     * @param ext 文件类型
     * @return 校验结果
     */
    public boolean isVideo(FileExtTypeEnum ext) {
        return FileExtTypeEnum.VIDEO.equals(ext);
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
     * 校验文件是否是音频
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public boolean isAudio(FileExtTypeEnum ext) {
        return FileExtTypeEnum.AUDIO.equals(ext);
    }

    /**
     * 校验文件是否是 word
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public boolean isWord(String ext) {
        return acquireFileExtProperty().acquireWordSupports().contains(ext);
    }

    /**
     * 校验文件是否是 word
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public boolean isWord(FileExtTypeEnum ext) {
        return FileExtTypeEnum.WORD.equals(ext);
    }

    /**
     * 校验文件是否是 ppt
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public boolean isPpt(String ext) {
        return acquireFileExtProperty().acquirePptSupports().contains(ext);
    }

    /**
     * 校验文件是否是 ppt
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public boolean isPpt(FileExtTypeEnum ext) {
        return FileExtTypeEnum.PPT.equals(ext);
    }

    /**
     * 校验文件是否是 excel
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public boolean isExcel(String ext) {
        return acquireFileExtProperty().acquireExcelSupports().contains(ext);
    }

    /**
     * 校验文件是否是 excel
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public boolean isExcel(FileExtTypeEnum ext) {
        return FileExtTypeEnum.EXCEL.equals(ext);
    }

    /**
     * 校验文件是否是 pdf
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public boolean isPdf(String ext) {
        return acquireFileExtProperty().acquirePdfSupports().contains(ext);
    }

    /**
     * 校验文件是否是 pdf
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public boolean isPdf(FileExtTypeEnum ext) {
        return FileExtTypeEnum.PDF.equals(ext);
    }

    /**
     * 检测文件是否是 txt
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    private boolean isTxt(String ext) {
        return acquireFileExtProperty().acquireTxtSupports().contains(ext);
    }

    /**
     * 检测文件是否是 txt
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    private boolean isTxt(FileExtTypeEnum ext) {
        return FileExtTypeEnum.TXT.equals(ext);
    }

    /**
     * 校验文件是否是 office 文档
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public boolean isDocument(String ext) {
        return acquireFileExtProperty().acquireDocumentSupports().contains(ext);
    }

    /**
     * 校验文件是否是 office 文档
     *
     * @param ext 文件后缀名
     * @return 校验结果
     */
    public boolean isDocument(FileExtTypeEnum ext) {
        return isWord(ext) || isPpt(ext) || isExcel(ext) || isPpt(ext) || isTxt(ext);
    }

    /**
     * 获取文件对应的文档类型
     *
     * @param ext 文件后缀格式
     * @return 文档类型
     */
    public FileExtTypeEnum acquireDocumentType(String ext) {
        if (isWord(ext)) {
            return FileExtTypeEnum.WORD;
        }
        if (isPpt(ext)) {
            return FileExtTypeEnum.PPT;
        }
        if (isExcel(ext)) {
            return FileExtTypeEnum.EXCEL;
        }
        if (isPdf(ext)) {
            return FileExtTypeEnum.PDF;
        }
        if (isTxt(ext)) {
            return FileExtTypeEnum.TXT;
        }
        return null;
    }

    /**
     * 获取文件对应的文档类型
     *
     * @param ext 文件后缀格式
     * @return 文档类型
     */
    public FileExtTypeEnum acquireFileType(String ext) {
        if (isVideo(ext)) {
            return FileExtTypeEnum.VIDEO;
        }
        if (isAudio(ext)) {
            return FileExtTypeEnum.AUDIO;
        }
        return acquireDocumentType(ext);
    }

    /**
     * 根据文件类型获取对应的后缀格式列表
     *
     * @param ext 文件类型后缀格式
     * @return 后缀格式列表
     */
    public List<String> acquireExtList(FileExtTypeEnum ext) {
        if (ObjUtil.isNull(ext)) {
            return ListUtil.empty();
        }
        if (FileExtTypeEnum.VIDEO.equals(ext)) {
            return acquireFileExtProperty().acquireVideoSupports();
        }
        if (FileExtTypeEnum.AUDIO.equals(ext)) {
            return acquireFileExtProperty().acquireAudioSupports();
        }
        if (FileExtTypeEnum.WORD.equals(ext)) {
            return acquireFileExtProperty().acquireWordSupports();
        }
        if (FileExtTypeEnum.PPT.equals(ext)) {
            return acquireFileExtProperty().acquirePptSupports();
        }
        if (FileExtTypeEnum.EXCEL.equals(ext)) {
            return acquireFileExtProperty().acquireExcelSupports();
        }
        if (FileExtTypeEnum.PDF.equals(ext)) {
            return acquireFileExtProperty().acquirePdfSupports();
        }
        if (FileExtTypeEnum.TXT.equals(ext)) {
            return acquireFileExtProperty().acquireTxtSupports();
        }
        return ListUtil.empty();
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
