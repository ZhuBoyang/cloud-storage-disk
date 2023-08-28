package online.yangcloud.common.tools;

import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.common.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * ip 工具
 *
 * @author zhuboyang
 * @since 2023/8/14 22:15
 */
public class IpTools {

    private static final Logger logger = LoggerFactory.getLogger(IpTools.class);
    private static final String IP = "127.0.0.1";
    private static final String UNKNOWN = "unknown";

    /**
     * 根据请求获取客户端 ip 地址
     *
     * @param req 客户端请求
     * @return ip 地址
     * @author zhuby
     * @since 2020/11/13 11:17 上午
     */
    public static String getIpAddress(HttpServletRequest req) throws UnknownHostException {
        String ipAddress = req.getHeader(AppConstants.Ip.X_FORWARDED_FOR);
        if (StrUtil.isBlank(ipAddress) || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getHeader(AppConstants.Ip.X_CLIENT_IP);
        }
        if (StrUtil.isBlank(ipAddress) || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getHeader(AppConstants.Ip.CLIENT_IP);
        }
        if (StrUtil.isBlank(ipAddress) || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getHeader(AppConstants.Ip.X_REAL_IP);
        }
        if (StrUtil.isBlank(ipAddress) || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getHeader(AppConstants.Ip.PROXY_CLIENT_IP);
        }
        if (StrUtil.isBlank(ipAddress) || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getHeader(AppConstants.Ip.WL_PROXY_CLIENT_IP);
        }
        if (StrUtil.isBlank(ipAddress) || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getRemoteAddr();
            if (IP.equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                InetAddress inet;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    System.out.println("get ip address : " + e.getMessage());
                    logger.info("ip address error : {}", e.getMessage());
                    throw e;
                }
                if (Objects.nonNull(inet)) {
                    ipAddress = inet.getHostAddress();
                }
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        // "***.***.***.***".length() = 15
        if (StrUtil.isNotBlank(ipAddress)) {
            if (ipAddress.indexOf(StrUtil.COMMA) > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(StrUtil.COMMA));
            }
        }
        if (AppConstants.Ip.LOCAL_IPV6_ADDRESS.equals(ipAddress)) {
            ipAddress = AppConstants.Ip.LOCAL_IPV4_ADDRESS;
        }
        return ipAddress;
    }

//    /**
//     * 从请求头以及参数中获取访问者的 ip 地址以及归属地
//     *
//     * @param request 请求
//     * @return result
//     */
//    public static VisitorIpInfo parseIpInfoFromRequestHeader(HttpServletRequest request) {
//        String ipBase64 = request.getParameter("i");
//        if (StrUtil.isBlank(ipBase64)) {
//            String ip = StrUtil.EMPTY;
//            try {
//                ip = getIpAddress(request);
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//            }
//            return new VisitorIpInfo(request.getHeader("User-Agent"), ip, StrUtil.EMPTY);
//        }
//        String ipAndName = new String(Base64Utils.decodeFromString(ipBase64), StandardCharsets.UTF_8);
//
//        ipBase64 = ipAndName.substring(0, ipAndName.indexOf(StrUtil.COMMA));
//        String ip = new String(Base64Utils.decodeFromString(ipBase64), StandardCharsets.UTF_8);
//        String ipAddressBase64 = ipAndName.substring(ipAndName.indexOf(StrUtil.COMMA) + 1);
//        String address = new String(Base64Utils.decodeFromString(ipAddressBase64), StandardCharsets.UTF_8);
//
//        return new VisitorIpInfo(request.getHeader("User-Agent"), ip, address);
//    }

}
