package online.yangcloud.common.database;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import online.yangcloud.common.annotation.DatabaseColumn;
import online.yangcloud.common.annotation.DatabaseIndex;
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
        Field[] classFields = clazz.getDeclaredFields();

        // 获取继承的父类
        Class<?> superclass = clazz.getSuperclass();

        // 获取父类的所有属性
        Field[] superFields = superclass.getDeclaredFields();

        // 获取实体类对应表的信息
        FluentMybatis tableAnnotation = clazz.getAnnotation(FluentMybatis.class);

        // 表结构 SQL 构建器
        StrBuilder tableGenerator = StrBuilder.create("CREATE TABLE `").append(tableAnnotation.table()).append("` (\n");
        // 主键 SQL 构建器
        StrBuilder primaryGenerator = StrBuilder.create("  PRIMARY KEY (`");
        // 索引 SQL 构建器
        StrBuilder indexGenerator = StrBuilder.create();

        // 循环实体类的所有属性，构建 SQL
        generateTableSql(tableAnnotation.table(), tableGenerator, primaryGenerator, indexGenerator, classFields);
        // 循环父类的所有属性，构建 SQL
        generateTableSql(tableAnnotation.table(), tableGenerator, primaryGenerator, indexGenerator, superFields);

        // 整合 SQL 构建器
        tableGenerator.append(primaryGenerator);
        tableGenerator.append(indexGenerator.subString(0, indexGenerator.length() - 2)).append("\n");
        tableGenerator.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='")
                .append(tableAnnotation.desc())
                .append("'\n");
        return tableGenerator.toString();
    }

    private void generateTableSql(String name, StrBuilder table, StrBuilder primary, StrBuilder index, Field[] fields) {
        for (Field field : fields) {
            DatabaseColumn columnAnnotation = field.getAnnotation(DatabaseColumn.class);
            // 字段名
            table.append("  `").append(columnAnnotation.name()).append("` ");
            // 字段类型
            table.append(columnAnnotation.type());
            if (AppConstants.Table.COLUMN_TYPES.contains(columnAnnotation.type())) {
                table.append("(").append(columnAnnotation.length()).append(")");
            }
            table.append(" ");
            // 字段编码格式
            if (AppConstants.Table.COLUMN_TYPES.contains(columnAnnotation.type())) {
                table.append("CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ");
            }
            // 是否可为空
            if (columnAnnotation.canNull()) {
                table.append("DEFAULT '").append(columnAnnotation.defaultValue()).append("' ");
            } else {
                table.append("NOT NULL ");
            }
            // 备注
            table.append("COMMENT '").append(columnAnnotation.comment()).append("',\n");

            // 字段是否为主键
            if (columnAnnotation.primary()) {
                primary.append(columnAnnotation.name()).append("`),\n");
            }
            // 是否设置索引
            DatabaseIndex indexAnnotation = field.getAnnotation(DatabaseIndex.class);
            if (ObjectUtil.isNotNull(indexAnnotation)) {
                // 是否是唯一索引
                index.append("  ");
                if (indexAnnotation.unique()) {
                    index.append("UNIQUE ");
                }
                index.append("KEY ")
                        .append("`").append(name).append("_").append(columnAnnotation.name()).append("_index").append("` ")
                        .append("(`").append(columnAnnotation.name()).append("`),\n");
            }
        }
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
