package online.yangcloud.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import online.yangcloud.common.constants.AppConstants;

import java.util.Calendar;
import java.util.Date;

/**
 * 数据校验工具类
 *
 * @author zhuboyang
 * @since 2023年03月17 14:43:16
 */
public class ValidateTools {

    /**
     * 校验字符串是否为邮箱
     *
     * @param email 待检测邮箱地址
     * @return result
     */
    public static boolean validEmail(String email) {
        return ReUtil.isMatch(AppConstants.Email.EMAIL_REGULAR, email);
    }

    /**
     * 检验字符串是否为手机号
     *
     * @param phone 待检测手机号
     * @return result
     */
    public static boolean validPhone(String phone) {
        return ReUtil.isMatch(AppConstants.PHONE_REGULAR, phone);
    }

    /**
     * 校验数据是否为空
     *
     * @param obj 数据对象
     */
    public static void validObjIsNotFound(Object obj) {
        if (ObjectUtil.isNull(obj)) {
            ExceptionTools.noDataLogger();
        }
    }

    /**
     * 根据生日计算年龄
     *
     * @param birthday 生日
     * @return 年龄
     */
    public static int calculateAge(Date birthday) {
        Calendar cal = Calendar.getInstance();
        // 出生日期晚于当前时间，无法计算
        if (cal.before(birthday)) {
            ExceptionTools.businessLogger("The birthDay is before Now.It's unbelievable!");
        }
        // 当前年份
        int yearNow = cal.get(Calendar.YEAR);
        // 当前月份
        int monthNow = cal.get(Calendar.MONTH);
        // 当前日期
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthday);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth; //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    // 当前日期在生日之前，年龄减一
                    age--;
                }
            } else {
                // 当前月份在生日之前，年龄减一
                age--;
            }
        }
        return age;
    }

}
