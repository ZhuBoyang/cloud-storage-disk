package online.yangcloud.common.common;

import online.yangcloud.common.enumration.DatabaseColumnTypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 应用变量，不可修改
 *
 * @author zhuboyang
 * @since 2023/8/15 16:47
 */
public interface AppConstants {

    /**
     * 特殊符号
     */
    interface Special {
        String PERCENT = "%";
        String POUND = "#";
        String EQUAL = "=";
        String LEFT_BRACKET = "（";
        String RIGHT_BRACKET = "）";
        String BRACKET_LEFT = "(";
        String BRACKET_RIGHT = ")";
        // 各平台文件名分隔符
        String SEPARATOR = File.separator;
    }

    /**
     * 执行耗时
     */
    interface Consuming {
        long NORMAL_TIMING = 1000L;
        long WARN_TIMING = 2 * NORMAL_TIMING;
    }

    /**
     * 正则表达式
     */
    interface Regex {
        // 手机号
        String PHONE_REGULAR = "^(0|86|17951)?(13[0-9]|15[012356789]|16[6]|19[89]]|17[01345678]|18[0-9]|14[579])[0-9]{8}$";
        // 邮箱正则
        String EMAIL_REGULAR = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    }

    /**
     * 数据表
     */
    interface Table {
        // 用户表
        String USER = "User";
        // 文件元数据表
        String FILE_METADATA = "FileMetadata";
        // 文件块元数据表
        String BLOCK_METADATA = "BlockMetadata";
        // 文件与文件块关联表
        String FILE_BLOCK = "FileBlock";
        // 视频元数据表
        String VIDEO_METADATA = "VideoMetadata";
        // 需要显示字段长度的类型
        List<DatabaseColumnTypeEnum> COLUMN_TYPES = Collections.singletonList(DatabaseColumnTypeEnum.VARCHAR);
    }

    /**
     * 用户账户相关
     */
    interface Account {
        // 加密次数
        Integer ENCRYPT_COUNT = 10;
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
        int COUNT = 500;
    }

    /**
     * 上传文件相关
     */
    interface Uploader {
        // 文件块上传缓存
        String FILE_BLOCK_UPLOAD_PREFIX = "file_block_upload:";
        // 总上传目录
        String UPLOAD = "upload" + File.separator;
        // 文件上传目录
        String FILE = UPLOAD + "file" + File.separator;
        // 文件块上传目录
        String BLOCK = UPLOAD + "block" + File.separator;
        // 临时文件存放目录
        String TMP = "tmp" + File.separator;
        // 缩略图存放目录
        String SNAPSHOT = "snapshot" + Special.SEPARATOR;
    }

    /**
     * 页面展示图标
     */
    interface Icon {
        // 默认宽度
        float DEFAULT_WIDTH = 500f;
    }

}
