package online.yangcloud.common.common.constants;

import cn.hutool.core.util.StrUtil;

import java.io.File;

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
    interface Special {
        String POUND = "#";
        String EQUAL = "=";
        // 各平台文件名分隔符
        String SEPARATOR = File.separator;
    }

    /**
     * 执行耗时
     */
    long NORMAL_TIMING = 1000L;
    long WARN_TIMING = 2 * NORMAL_TIMING;

    /**
     * 手机号正则
     */
    String PHONE_REGULAR = "^(0|86|17951)?(13[0-9]|15[012356789]|16[6]|19[89]]|17[01345678]|18[0-9]|14[579])[0-9]{8}$";

    /**
     * 邮箱相关
     */
    interface Email {
        // 邮箱验证码过期时间（时间单位：m）
        Integer EMAIL_CODE_EXPIRED_TIME = 5;
        // 用于注册的邮箱验证码的 redis key
        String REGISTER_EMAIL_REDIS_KEY = "register_email_code:";
        // 用于重置密码的邮箱验证码的 redis key
        String RESET_PASSWORD_REDIS_KEY = "reset_password:";
        // 邮箱正则
        String EMAIL_REGULAR = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    }

    /**
     * 用户账户相关
     */
    interface User {
        String LOGIN_TOKEN = "token:";
        // 账号登录 session 会话过期时间：7 * 24 小时
        Integer LOGIN_SESSION_EXPIRE_TIME = 7 * 24 * 60 * 60;
        // 会话标识
        String AUTHORIZATION = "authorization";
        // 账户总容量
        Long TOTAL_SPACE_SIZE = 5 * 1024 * 1024 * 1024L;
        // 登录状态续期
        Long ACCOUNT_EXPIRED_STATUS = -1L;
        Long ACCOUNT_NOT_EXIST_STATUS = -2L;
        // 账户空间
        String SPACE_UPDATE = "space_update:";
    }

    /**
     * ip 地址相关
     */
    interface Ip {
        String X_FORWARDED_FOR = "x-forwarded-for";
        String X_CLIENT_IP = "x-client-ip";
        String CLIENT_IP = "client-ip";
        String X_REAL_IP = "x-real-ip";
        String PROXY_CLIENT_IP = "Proxy-Client-IP";
        String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
        // 本地 ipv4 地址
        String LOCAL_IPV4_ADDRESS = "127.0.0.1";
        // 本地 ipv6 地址，如果后期不需要，可以不使用
        String LOCAL_IPV6_ADDRESS = "0:0:0:0:0:0:0:1";
        // 访问者 ip 地址前缀
        String VISITOR_IP = "visit_ip:";
    }

    /**
     * 批量操作相关
     */
    interface Batch {
        // 单次批量插入数据的最大数据量
        Integer COUNT = 500;
    }

    /**
     * 上传文件相关
     */
    interface Uploader {
        /**
         * 文件块上传缓存
         */
        String FILE_BLOCK_UPLOAD_PREFIX = "file_block_upload:";
        String FILE_BLOCK_UPLOAD_BUFFER_PREFIX = "file_block_buffer_upload:";
        /**
         * 文件块是否已上传检测
         */
        String BLOCK_METADATA_PREFIX = "block_metadata:";
        /**
         * 文件上传目录
         */
        String FILE_UPLOAD_PATH = "upload" + File.separator + "file" + File.separator;
        /**
         * 文件块上传目录
         */
        String BLOCK_UPLOAD_PATH = "upload" + File.separator + "block" + File.separator;
        /**
         * 临时文件存放目录
         */
        String TMP_PATH = "tmp" + File.separator;
    }

    /**
     * 文件相关
     */
    interface FileMetadata {
        // 祖级文件 id 隔断符
        String ANCESTOR_SEPARATOR = StrUtil.UNDERLINE;
    }

}
