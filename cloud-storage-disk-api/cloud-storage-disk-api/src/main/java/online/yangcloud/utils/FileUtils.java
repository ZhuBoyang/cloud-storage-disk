package online.yangcloud.utils;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Vector;

/**
 * 文件工具类
 *
 * @author zhuboyang
 * @since 2023年01月03 13:39:25
 */
public class FileUtils {

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

}
