package online.yangcloud.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhuboyang
 * @since 2022年10月27 11:26:44
 */
public class RainIoUtil {

    /**
     * 将 InputStream 转为 byte 数组
     *
     * @param input        输入流
     * @param streamLength 字节长度
     * @return 字节数组
     * @throws IOException IOException
     */
    public static byte[] toByteArray(InputStream input, Integer streamLength) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[streamLength];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

}
