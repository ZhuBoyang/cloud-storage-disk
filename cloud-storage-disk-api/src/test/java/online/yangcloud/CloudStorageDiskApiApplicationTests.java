//package online.yangcloud;
//
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.IdUtil;
//import cn.hutool.extra.qrcode.QrCodeUtil;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.net.URL;
//
//@SpringBootTest
//class CloudStorageDiskApiApplicationTests {
//
//    @Test
//    void contextLoads() {
//        String filePath = "/Users/boyang/Documents/";
//
//        QrCodeUtil.generate(IdUtil.fastSimpleUUID(), 300, 500, FileUtil.file(filePath + "qrTest.png"));
//        generateCode(filePath);
//    }
//
//    public static String generateCode(String filePath) {
//        Font font = new Font("微软雅黑", Font.PLAIN, 30);// 添加字体的属性设置
//
//        String projectUrl = "/before/codeImg/";
//        String imgName = projectUrl + userId + ".png";
//        try {
//            // 生成设备二维码图片
//            BufferedImage qrCodeImage = QrCodeUtil.generate(IdUtil.fastSimpleUUID(), 300, 300);
//            
//
//            // 加载本地图片
//            String imageLocalUrl = projectUrl + "weixincode2.png";
//            BufferedImage imageLocal = ImageIO.read(new File(imageLocalUrl));
//            // 加载用户的二维码
//            BufferedImage imageCode = ImageIO.read(new URL(codeUrl));
//            // 以本地图片为模板
//            Graphics2D g = imageLocal.createGraphics();
//            // 在模板上添加用户二维码(地址,左边距,上边距,图片宽度,图片高度,未知)
//            g.drawImage(imageCode, 575, imageLocal.getHeight() - 500, 350, 350, null);
//            // 设置文本样式
//            g.setFont(font);
//            g.setColor(Color.BLACK);
//            // 截取用户名称的最后一个字符
//            String lastChar = userName.substring(userName.length() - 1);
//            // 拼接新的用户名称
//            String newUserName = userName.charAt(0) + "**" + lastChar + " 的邀请二维码";
//            // 添加用户名称
//            g.drawString(newUserName, 620, imageLocal.getHeight() - 530);
//            // 完成模板修改
//            g.dispose();
//            // 获取新文件的地址
//            File outputfile = new File(imgName);
//            // 生成新的合成过的用户二维码并写入新图片
//            ImageIO.write(imageLocal, "png", outputfile);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // 返回给页面的图片地址(因为绝对路径无法访问)
//        imgName = "codeImg/" + userId + ".png";
//
//        return imgName;
//    }
//
//}
