package online.yangcloud.common.tools;

import cn.hutool.core.util.StrUtil;

import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年09月11 14:54:49
 */
public class TimeTools {

    /**
     * 将时长转换为秒数（带有毫秒）
     *
     * @param duration 时长
     * @return 秒数
     */
    public static Double convert(String duration) {
        List<String> arr = StrUtil.split(duration, StrUtil.COLON);
        double value = 0d;

        // 解析小时
        int hour = Integer.parseInt(arr.get(0).trim());
        if (hour != 0) {
            value += hour * 60 * 60;
        }

        // 解析分钟
        int minute = Integer.parseInt(arr.get(1));
        if (minute != 0) {
            value += minute * 60;
        }

        // 解析秒
        int second = Integer.parseInt(arr.get(2).substring(0, arr.get(2).indexOf(StrUtil.DOT)));
        if (second != 0) {
            value += second;
        }

        // 解析毫秒
        double millisecond = Double.parseDouble(0 + arr.get(2).substring(arr.get(2).indexOf(StrUtil.DOT)));
        if (millisecond != 0) {
            value += millisecond;
        }

        return value;
    }

}
