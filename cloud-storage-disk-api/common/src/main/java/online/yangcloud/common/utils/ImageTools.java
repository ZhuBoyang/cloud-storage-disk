package online.yangcloud.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;

/**
 * @author zhuboyang
 * @since 2022/12/31 11:34
 */
public class ImageTools {

    private static final Logger logger = LoggerFactory.getLogger(ImageTools.class);

    public static byte[] readImage(String imgPath) {
        byte[] data = null;
        InputStream in = null;

        // 读取图片字节数组
        try {
            in = Files.newInputStream(Paths.get(imgPath));
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

    public static String imageToBase64(String imgPath, String fileType) {
        byte[] data = readImage(imgPath);

        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();

        // 返回Base64编码过的字节数组字符串
        return "data:" + fileType + ";base64," + encoder.encode(Objects.requireNonNull(data));
    }

    public static void base64ToStream(String base64) {
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

}
