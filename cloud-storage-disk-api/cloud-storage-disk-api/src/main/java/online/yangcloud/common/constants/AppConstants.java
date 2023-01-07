package online.yangcloud.common.constants;

/**
 * @author zhuby
 * @since 2020/12/31 10:20 上午
 */

public interface AppConstants {

    /**
     * 特殊符号
     */
    String PERCENT = "%";
    String LEFT_BRACKET = "（";
    String RIGHT_BRACKET = "）";

    /**
     * 执行耗时
     */
    long NORMAL_TIMING = 1000L;
    long WARN_TIMING = 2 * NORMAL_TIMING;

    /**
     * 登录状态续期
     */
    long ACCOUNT_RENEWAL_TIME = 5 * 60L;
    long ACCOUNT_EXPIRED_STATUS = -1L;
    long ACCOUNT_NOT_EXIST_STATUS = -2;

    /**
     * ip 相关
     */
    String X_FORWARDED_FOR = "x-forwarded-for";
    String X_CLIENT_IP = "x-client-ip";
    String CLIENT_IP = "client-ip";
    String X_REAL_IP = "x-real-ip";
    String PROXY_CLIENT_IP = "Proxy-Client-IP";
    String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

    /**
     * 本地 ipv4 地址
     */
    String LOCAL_IPV4_ADDRESS = "127.0.0.1";

    /**
     * 本地 ipv6 地址，如果后期不需要，可以不使用
     */
    String LOCAL_IPV6_ADDRESS = "0:0:0:0:0:0:0:1";

    /**
     * 访问者 ip 地址前缀
     */
    String VISITOR_IP_PREFIX = "visit_ip:";

    /**
     * 文件块上传缓存
     */
    String FILE_BLOCK_UPLOAD_PREFIX = "file_block_upload:";
    String FILE_BLOCK_UPLOAD_BUFFER_PREFIX = "file_block_buffer_upload:";

}
