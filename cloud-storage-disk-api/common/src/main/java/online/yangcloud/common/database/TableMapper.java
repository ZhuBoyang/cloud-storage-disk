package online.yangcloud.common.database;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import online.yangcloud.common.common.AppConstants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhuboyang
 * @since 2023年09月03 17:13:20
 */
@Repository
public class TableMapper {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询连接的数据库下现存有几张数据表
     *
     * @return 数据表名列表
     */
    public List<String> queryTablesName() {
        List<String> tablesName = new ArrayList<>();
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("show tables; ");
        maps.forEach(o -> o.forEach((k, v) -> tablesName.add(JSONUtil.toJsonStr(v))));
        return tablesName;
    }

    /**
     * 根据表名生成创建表结构的 SQL
     *
     * @param tableName 表名
     * @return 可执行 SQL
     */
    public String generateTableSql(String tableName) {
        if (AppConstants.Table.USER.equals(tableName)) {
            return generateUserTable();
        }
        if (AppConstants.Table.FILE_METADATA.equals(tableName)) {
            return generateFileMetadata();
        }
        if (AppConstants.Table.BLOCK_METADATA.equals(tableName)) {
            return generateBlockMetadata();
        }
        if (AppConstants.Table.FILE_BLOCK.equals(tableName)) {
            return generateFileBlock();
        }
        if (AppConstants.Table.VIDEO_METADATA.equals(tableName)) {
            return generateVideoMetadata();
        }
        return StrUtil.EMPTY;
    }

    /**
     * 生成‘用户表’的表结构 SQL
     *
     * @return 用户表 SQL
     */
    public String generateUserTable() {
        return StrBuilder.create()
                .append("CREATE TABLE `user` (")
                .append("  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户 id',")
                .append("  `nick_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '昵称',")
                .append("  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',")
                .append("  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账户密码',")
                .append("  `avatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '头像地址',")
                .append("  `birthday` bigint DEFAULT '0' COMMENT '出生日期',")
                .append("  `age` smallint DEFAULT '0' COMMENT '年龄',")
                .append("  `gender` tinyint DEFAULT (-(1)) COMMENT '性别',")
                .append("  `phone` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '联系方式',")
                .append("  `total_space_size` bigint DEFAULT '0' COMMENT '账户总空间',")
                .append("  `used_space_size` bigint DEFAULT '0' COMMENT '账户已用空间',")
                .append("  `create_time` bigint NOT NULL,")
                .append("  `update_time` bigint NOT NULL,")
                .append("  `is_delete` tinyint DEFAULT '0',")
                .append("  PRIMARY KEY (`id`),")
                .append("  UNIQUE KEY `User_pk2` (`id`)")
                .append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表'")
                .toString();
    }

    /**
     * 生成‘文件元数据’表结构 SQL
     *
     * @return 文件元数据 SQL
     */
    public String generateFileMetadata() {
        return StrBuilder.create()
                .append("CREATE TABLE `file_metadata` (")
                .append("  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件 id',")
                .append("  `pid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '上级文件 id',")
                .append("  `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',")
                .append("  `path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件存储路径',")
                .append("  `hash` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件 hash',")
                .append("  `type` tinyint NOT NULL COMMENT '文件类型：0.文件；1.文件夹',")
                .append("  `ext` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件后缀名',")
                .append("  `size` bigint NOT NULL COMMENT '文件大小',")
                .append("  `upload_time` bigint NOT NULL COMMENT '上传时间',")
                .append("  `ancestors` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '祖级文件 id 列表',")
                .append("  `is_delete` tinyint DEFAULT '0',")
                .append("  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件操作人 id',")
                .append("  `create_time` bigint NOT NULL,")
                .append("  `update_time` bigint NOT NULL,")
                .append("  PRIMARY KEY (`id`),")
                .append("  UNIQUE KEY `file_metadata_pk2` (`id`)")
                .append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件元数据'")
                .toString();
    }

    /**
     * 生成’文件块元数据‘表结构 SQL
     *
     * @return 文件块元数据 SQL
     */
    public String generateBlockMetadata() {
        return StrBuilder.create()
                .append("CREATE TABLE `block_metadata` (")
                .append("  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件块 id',")
                .append("  `hash` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件块 hash',")
                .append("  `path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件块存储路径',")
                .append("  `size` bigint NOT NULL COMMENT '文件块大小',")
                .append("  `create_time` bigint NOT NULL,")
                .append("  `update_time` bigint NOT NULL,")
                .append("  `is_delete` tinyint DEFAULT '0',")
                .append("  PRIMARY KEY (`id`),")
                .append("  UNIQUE KEY `block_metadata_pk2` (`id`)")
                .append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件块元数据'")
                .toString();
    }

    /**
     * 生成’文件与文件块关联‘表结构 SQL
     *
     * @return 文件与文件块关联 SQL
     */
    public String generateFileBlock() {
        return StrBuilder.create()
                .append("CREATE TABLE `file_block` (")
                .append("  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联 id',")
                .append("  `file_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件 id',")
                .append("  `block_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件块 id',")
                .append("  `index` smallint NOT NULL COMMENT '文件块序号',")
                .append("  `count` smallint NOT NULL COMMENT '文件块数量',")
                .append("  `sharding_size` bigint NOT NULL COMMENT '分片大小',")
                .append("  `file_size` bigint NOT NULL COMMENT '文件总大小',")
                .append("  `is_shard` tinyint NOT NULL COMMENT '是否分片：0.不分片；1.分片',")
                .append("  `create_time` bigint NOT NULL,")
                .append("  `update_time` bigint NOT NULL,")
                .append("  `is_delete` tinyint DEFAULT '0',")
                .append("  PRIMARY KEY (`id`),")
                .append("  UNIQUE KEY `file_block_pk2` (`id`)")
                .append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件与文件块的关联'")
                .toString();
    }

    /**
     * 生成’视频元数据‘表结构 SQL
     *
     * @return 视频元数据 SQL
     */
    public String generateVideoMetadata() {
        return StrBuilder.create()
                .append("CREATE TABLE `video_metadata` (")
                .append("  `id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL,")
                .append("  `file_id` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件 id',")
                .append("  `thumbnail` varchar(128) COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频缩略图地址',")
                .append("  `total_frames` int NOT NULL COMMENT '总帧数',")
                .append("  `frame_rate` double NOT NULL COMMENT '帧率',")
                .append("  `duration` double NOT NULL COMMENT '总时长',")
                .append("  `video_code` varchar(10) COLLATE utf8mb4_general_ci NOT NULL COMMENT '视频编码',")
                .append("  `audio_code` varchar(10) COLLATE utf8mb4_general_ci NOT NULL COMMENT '音频编码',")
                .append("  `width` smallint NOT NULL COMMENT '视频宽度',")
                .append("  `height` smallint NOT NULL COMMENT '视频高度',")
                .append("  `audio_channel` smallint NOT NULL COMMENT '音频通道',")
                .append("  `sample_rate` int NOT NULL COMMENT '音频采样率',")
                .append("  `create_time` bigint NOT NULL,")
                .append("  `update_time` bigint NOT NULL,")
                .append("  `is_delete` tinyint DEFAULT '0',")
                .append("  PRIMARY KEY (`id`),")
                .append("  UNIQUE KEY `video_metadata_pk2` (`id`),")
                .append("  KEY `video_metadata_file_id_index` (`file_id`)")
                .append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci")
                .toString();
    }

}
