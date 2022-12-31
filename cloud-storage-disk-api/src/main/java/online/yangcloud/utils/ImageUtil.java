package online.yangcloud.utils;

import cn.hutool.core.io.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * @author zhuboyang
 * @since 2022年05月27 17:13:39
 */
public class ImageUtil {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    public static byte[] readImage(String imgPath) {
        byte[] data = null;
        InputStream in = null;

        // 读取图片字节数组
        try {
            in = new FileInputStream(imgPath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public static void saveImage(byte[] imageByte) {
        InputStream input = null;

        try {
            //转化成流
            input = new ByteArrayInputStream(imageByte);
            BufferedImage bi = ImageIO.read(input);
            File file = new File("temp.png");
            ImageIO.write(bi, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String ImageToBase64(String imgPath, String fileType) {
        byte[] data = readImage(imgPath);

        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();

        // 返回Base64编码过的字节数组字符串
        return "data:" + fileType + ";base64," + encoder.encode(Objects.requireNonNull(data));
    }

    public static void Base64ToStream(String base64) {
        BASE64Decoder decoder = new BASE64Decoder();

        byte[] imageByte = new byte[0];

        try {
            imageByte = decoder.decodeBuffer(base64);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //图片类型
        saveImage(imageByte);
    }

    public static String transformPhotoToBase64Data(String filePath) {
        // 获取Base64编码器
        Base64.Encoder encoder = Base64.getEncoder();
        // 数据集缓存器
        byte[] imgContainer;
        // 文件输入流
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            System.out.println(filePath);
            // 到指定路径寻找文件
            // 设置图片字节数据缓冲区大小
            imgContainer = new byte[fileInputStream.available()];
            // 将数据流中的图片数据读进缓冲区
            fileInputStream.read(imgContainer);
            // 将图片编码转换成Base64格式的数据集
            String base64ImgData = encoder.encodeToString(imgContainer);
            // 关闭数据流
            fileInputStream.close();
            // 将缓冲区数据转换成字符数据返回
            return base64ImgData;
        } catch (FileNotFoundException e) {
            return "找不到指定文件!";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public static boolean merge(String[] imgs, String type, String mergePic) {
        int dstHeight = 0;
        int dstWidth = 0;
        // 获取需要拼接的图片长度
        int len = imgs.length;
        // 判断长度是否大于0
        if (len < 1) {
            return false;
        }
        File[] file = new File[len];
        BufferedImage[] images = new BufferedImage[len];
        int[][] ImageArrays = new int[len][];
        for (int i = 0; i < len; i++) {
            try {
                file[i] = new File(imgs[i]);
                images[i] = ImageIO.read(file[i]);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            int width = images[i].getWidth();
            int height = images[i].getHeight();

            // 从图片中读取RGB 像素
            ImageArrays[i] = new int[width * height];
            ImageArrays[i] = images[i].getRGB(0, 0, width, height, ImageArrays[i], 0, width);

            // 计算合并的宽度和高度
            dstWidth = Math.max(dstWidth, width);
            dstHeight += height;
        }

        if (dstHeight < 1) {
            System.out.println("dstHeight < 1");
            return false;
        }
        // 生成新图片
        try {
            BufferedImage imageNew = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_INT_RGB);
            int height_i = 0;
            for (int i = 0; i < images.length; i++) {
                int width = images[i].getWidth();
                int height = images[i].getHeight();
                imageNew.setRGB(0, height_i, width, height, ImageArrays[i], 0, width);
                height_i += height;
            }

            File outFile = new File(mergePic);
            // 写图片，输出到硬盘
            ImageIO.write(imageNew, type, outFile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void mergeFile(List<String> sources, String target) throws IOException {
//        int indexOf = cutFileName.indexOf("-");
//        String timeStream = cutFileName.substring(0, indexOf);
        //段数+文件名+后缀名
//        String substring = cutFileName.substring(indexOf + 1);
//        int indexOf1 = substring.indexOf("-");
        //文件名+后缀名
//        String fileName = substring.substring(indexOf1 + 1);
        if (FileUtil.exist(target)) {
            FileUtil.del(target);
            logger.info("覆盖已经存在的文件");
        }
        BufferedOutputStream destOutputStream = new BufferedOutputStream(new FileOutputStream(target));
        for (String source : sources) {
            byte[] fileBuffer = new byte[1024];
            int readBytesLength;
            BufferedInputStream sourceStream = new BufferedInputStream(new FileInputStream(source));
            logger.info("开始合并分段文件：{}", source);
            while ((readBytesLength = sourceStream.read(fileBuffer)) != -1) {
                destOutputStream.write(fileBuffer, 0, readBytesLength);
            }
            sourceStream.close();
        }
//        for (int i = 1; i <= chunks; i++) {
//            //循环将每个分片的数据写入目标文件
//            byte[] fileBuffer = new byte[1024];//文件读写缓存
//            int readBytesLength; //每次读取字节数
//            File sourceFile = new File(saveAddr + timeStream + "-" + i + "-" + fileName);
//            BufferedInputStream sourceInputStream = new BufferedInputStream(new FileInputStream(sourceFile));
//            logger.info("开始合并分段文件：" + timeStream + "-" + i + "-" + fileName);
//            while ((readBytesLength = sourceInputStream.read(fileBuffer)) != -1) {
//                destOutputStream.write(fileBuffer, 0, readBytesLength);
//            }
//            sourceInputStream.close();
//            logger.info("合并分段文件完成：" + timeStream + "-" + i + "-" + fileName);
////            //分片合并后删除
////            boolean delete = sourceFile.delete();
////            if (delete) {
////                LOGGER.info(timeStream + "-" + i + "-" + fileName + "删除完成");
////            }
//        }
        destOutputStream.flush();
        destOutputStream.close();
    }


}
