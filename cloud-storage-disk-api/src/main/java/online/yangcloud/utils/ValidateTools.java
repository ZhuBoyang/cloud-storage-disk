package online.yangcloud.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import online.yangcloud.common.constants.AppConstants;

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

}
