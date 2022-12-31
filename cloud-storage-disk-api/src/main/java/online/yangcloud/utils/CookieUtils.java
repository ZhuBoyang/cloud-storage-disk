package online.yangcloud.utils;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * cookie工具类
 *
 * @author zhuby
 * @date 2020/8/20 4:13 下午
 */

public class CookieUtils {

    private CookieUtils() {
    }

    final static Logger logger = LoggerFactory.getLogger(CookieUtils.class);
    private static final String LOCALHOST_STRING = "localhost";

    /**
     * Description: 得到Cookie的值, 不编码
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        return getCookieValue(request, cookieName, false);
    }

    /**
     * Description: 得到Cookie的值
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName, boolean isDecoder) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (Cookie cookie : cookieList) {
                if (cookie.getName().equals(cookieName)) {
                    if (isDecoder) {
                        retValue = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    } else {
                        retValue = cookie.getValue();
                    }
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    /**
     * Description: 得到Cookie的值
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName, String encodeString) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (Cookie cookie : cookieList) {
                if (cookie.getName().equals(cookieName)) {
                    retValue = URLDecoder.decode(cookie.getValue(), encodeString);
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    /**
     * Description: 设置Cookie的值 不设置生效时间默认浏览器关闭即失效,也不编码
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue) {
        setCookie(request, response, cookieName, cookieValue, -1);
    }

    /**
     * Description: 设置Cookie的值 在指定时间内生效,但不编码
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, int cookieMaxAge) {
        setCookie(request, response, cookieName, cookieValue, cookieMaxAge, false);
    }

    /**
     * Description: 设置Cookie的值 不设置生效时间,但编码
     * 在服务器被创建，返回给客户端，并且保存客户端
     * 如果设置了SET_MAX_AGE(int seconds)，会把cookie保存在客户端的硬盘中
     * 如果没有设置，会默认把cookie保存在浏览器的内存中
     * 一旦设置setPath()：只能通过设置的路径才能获取到当前的cookie信息
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, boolean isEncode) {
        setCookie(request, response, cookieName, cookieValue, -1, isEncode);
    }

    /**
     * Description: 设置Cookie的值 在指定时间内生效, 编码参数
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, int cookieMaxAge, boolean isEncode) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxAge, isEncode);
    }

    /**
     * Description: 设置Cookie的值 在指定时间内生效, 编码参数(指定编码)
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                 String cookieValue, int cookieMaxAge, String encodeString) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxAge, encodeString);
    }

    /**
     * Description: 删除Cookie带cookie域名
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        doSetCookie(request, response, cookieName, null, -1, false);
    }


    /**
     * Description: 设置Cookie的值，并使其在指定时间内生效
     *
     * @param cookieMaxAge cookie生效的最大秒数
     */
    private static void doSetCookie(HttpServletRequest request, HttpServletResponse response,
                                    String cookieName, String cookieValue, int cookieMaxAge, boolean isEncode) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else if (isEncode) {
                cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            }
            response.addCookie(createNewCookie(request, cookieMaxAge, cookieName, cookieValue));
        } catch (Exception e) {
            logger.error("url encode error {}", e.getMessage(), e);
        }
    }

    /**
     * Description: 设置Cookie的值，并使其在指定时间内生效
     *
     * @param cookieMaxAge cookie生效的最大秒数
     */
    private static void doSetCookie(HttpServletRequest request, HttpServletResponse response,
                                    String cookieName, String cookieValue, int cookieMaxAge, String encodeString) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else {
                cookieValue = URLEncoder.encode(cookieValue, encodeString);
            }
            response.addCookie(createNewCookie(request, cookieMaxAge, cookieName, cookieValue));
        } catch (Exception e) {
            logger.error("url encode error {}", e.getMessage(), e);
        }
    }

    public static Cookie createNewCookie(HttpServletRequest request, int cookieMaxAge,
                                         String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        if (cookieMaxAge > 0) {
            cookie.setMaxAge(cookieMaxAge);
        }
        // 设置域名的cookie
        if (null != request) {
            String domainName = getDomainName(request);
            logger.info("========== domainName: {} ==========", domainName);
            if (!CookieUtils.LOCALHOST_STRING.equals(domainName)) {
                cookie.setDomain(domainName);
            }
        }
        cookie.setPath(StrUtil.SLASH);
        return cookie;
    }

    /**
     * Description: 得到cookie的域名
     */
    private static String getDomainName(HttpServletRequest request) {
        String domainName;

        String serverName = request.getRequestURL().toString();
        if (StrUtil.EMPTY.equals(serverName)) {
            domainName = StrUtil.EMPTY;
        } else {
            serverName = serverName.toLowerCase();
            serverName = serverName.substring(7);
            final int end = serverName.indexOf(StrUtil.SLASH);
            serverName = serverName.substring(0, end);
            if (serverName.indexOf(StrUtil.COLON) > 0) {
                String[] ary = serverName.split(StrUtil.COLON);
                serverName = ary[0];
            }

            final String[] domains = serverName.split("\\.");
            int len = domains.length;
            if (len > 3 && !isIp(serverName)) {
                // www.xxx.com.cn
                domainName = StrUtil.DOT + domains[len - 3] + StrUtil.DOT + domains[len - 2] + StrUtil.DOT + domains[len - 1];
            } else if (len <= 3 && len > 1) {
                // xxx.com or xxx.cn
                domainName = StrUtil.DOT + domains[len - 2] + StrUtil.DOT + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }
        return domainName;
    }

    /**
     * 去掉IP字符串前后所有的空格
     */
    public static String trimSpaces(String ip) {
        while (ip.startsWith(StrUtil.SPACE)) {
            ip = ip.substring(1).trim();
        }
        while (ip.endsWith(StrUtil.SPACE)) {
            ip = ip.substring(0, ip.length() - 1).trim();
        }
        return ip;
    }

    /**
     * 判断地址是否一个ip地址
     *
     * @param ip ip地址
     * @return 是否是ip地址
     * @author zhuby
     * @since 2021/4/10 10:49
     */
    public static boolean isIp(String ip) {
        boolean b = false;
        ip = trimSpaces(ip);
        if (ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            String[] s = ip.split("\\.");
            if (Integer.parseInt(s[0]) < 255) {
                if (Integer.parseInt(s[1]) < 255) {
                    if (Integer.parseInt(s[2]) < 255) {
                        if (Integer.parseInt(s[3]) < 255) {
                            b = true;
                        }
                    }
                }
            }
        }
        return b;
    }
}
