package online.yangcloud.common.database;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import online.yangcloud.common.annotation.DatabaseColumn;
import online.yangcloud.common.common.AppConstants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.lang.reflect.Field;
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
     * 生成‘用户表’的表结构 SQL
     *
     * @return 用户表 SQL
     */
    public String generateUserTable(Class<?> clazz) {
        // 获取实体类的所有属性
        Field[] fields = clazz.getDeclaredFields();

        // 获取实体类对应表的信息
        FluentMybatis tableAnnotation = clazz.getAnnotation(FluentMybatis.class);

        // 构建表结构 SQL
        StrBuilder sqlGenerator = StrBuilder.create("CREATE TABLE `").append(tableAnnotation.table()).append("` (");
        String tableIdName = StrUtil.EMPTY;
        {
            for (Field field : fields) {
                TableId idAnnotation = field.getAnnotation(TableId.class);
                if (ObjectUtil.isNotNull(idAnnotation)) {
                    tableIdName = field.getName();
                }
                DatabaseColumn columnAnnotation = field.getAnnotation(DatabaseColumn.class);
                if (ObjectUtil.isNotNull(columnAnnotation)) {
                    // 字段名
                    sqlGenerator.append("  `").append(columnAnnotation.name()).append("` ");
                    // 字段类型
                    sqlGenerator.append(columnAnnotation.type());
                    // 字段长度
                    if (AppConstants.Table.COLUMN_TYPES.contains(columnAnnotation.type())) {
                        sqlGenerator.append("(").append(columnAnnotation.length()).append(")").append(" ");
                    } else {
                        sqlGenerator.append(" ");
                    }
                    // 编码格式
                    if (AppConstants.Table.COLUMN_TYPES.contains(columnAnnotation.type())) {
                        sqlGenerator.append("CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ");
                    }
                    // 是否为空，字段默认值
                    if (columnAnnotation.canNull()) {
                        sqlGenerator.append("DEFAULT ");
                        if (AppConstants.Table.COLUMN_TYPES.contains(columnAnnotation.type())) {
                            sqlGenerator.append("'").append(columnAnnotation.defaultValue()).append("'");
                        } else {
                            sqlGenerator.append(columnAnnotation.defaultValue());
                        }
                        sqlGenerator.append(" ");
                    } else {
                        sqlGenerator.append("NOT NULL ");
                    }
                    // 注释
                    if (StrUtil.isNotBlank(columnAnnotation.comment())) {
                        sqlGenerator.append("COMMENT '").append(columnAnnotation.comment()).append("'");
                    }
                    sqlGenerator.append(",");
                }
            }
        }
        sqlGenerator.append(generateBaseParameter());
        {
            sqlGenerator.append("  PRIMARY KEY (`").append(tableIdName).append("`),");
            sqlGenerator.append("  UNIQUE KEY `")
                    .append(tableAnnotation.table())
                    .append("_pk2` (`")
                    .append(tableIdName)
                    .append("`)");
            sqlGenerator.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci")
                    .append(StrUtil.isBlank(tableAnnotation.desc()) ? "" : " COMMENT='" + tableAnnotation.desc() + "'");
        }
        return sqlGenerator.toString();
    }

    /**
     * 生成基础字段 SQL
     *
     * @return 基础字段 SQL
     */
    private StrBuilder generateBaseParameter() {
        return StrBuilder.create()
                .append("  `create_time` bigint NOT NULL,")
                .append("  `update_time` bigint NOT NULL,")
                .append("  `is_delete` tinyint DEFAULT '0',");
    }

}
