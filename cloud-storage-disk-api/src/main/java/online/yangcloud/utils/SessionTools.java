package online.yangcloud.utils;

import online.yangcloud.common.constants.AppConstants;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhuboyang
 * @since 2023年06月13 15:59:04
 */
public class SessionTools {

    /**
     * 获取请求 token
     *
     * @param request 请求
     * @return token
     */
    public static String getSessionId(HttpServletRequest request) {
        return request.getHeader(AppConstants.User.AUTHORIZATION);
    }

}
