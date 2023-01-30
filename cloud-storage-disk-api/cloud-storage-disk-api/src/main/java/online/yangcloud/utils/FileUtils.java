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

    public static void combineFile(String target, List<String> sources) {
        try (FileOutputStream fos = new FileOutputStream(target)) {
            FileInputStream fis;
            // 一次读取 2M 数据，将读取到的数据保存到 byte 字节数组中
            byte[] buffer = new byte[1024 * 1024 * 2];
            int len;
            for (String fileName : sources) {
                fis = new FileInputStream(fileName);
                len = 0;
                while ((len = fis.read(buffer)) != -1) {
                    // buffer 从指定字节数组写入。buffer: 数据中的起始偏移量， len: 写入的字数。
                    fos.write(buffer, 0, len);
                }
                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("合并完成！");
        }
    }

}
