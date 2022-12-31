package online.yangcloud.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 线程安全下的时间日期格式化工具类
 *
 * @author zhuboyang
 * @since 2022/12/31 10:16
 */
public class SimpleDateFormatUtil {

    /**
     * 线程安全转换 String to Date
     */
    public static Date safeParseDateTime(String dateStr) throws ParseException {
        return getFormat().parse(dateStr);
    }

    /**
     * 线程安全格式化 Date to String
     */
    public static String safeFormatDateTime(Date date) {
        return getFormat().format(date);
    }

    /**
     * 借助 ThreadLocal 完成对每个线程第一次调用时初始化 SimpleDateFormat 对象
     */
    private static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected synchronized SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DatePattern.NORM_DATETIME_FORMAT.getPattern());
        }
    };

    /**
     * 获取当前线程中的安全 SimpleDateFormat 对象
     */
    private static DateFormat getFormat() {
        return THREAD_LOCAL.get();
    }

}
