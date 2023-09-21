package online.yangcloud.common.runner;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import online.yangcloud.common.database.TableMapper;
import online.yangcloud.common.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 初始化表结构启动器
 *
 * @author zhuboyang
 * @since 2023年09月03 15:04:57
 */
@Order(1)
@Component
public class TableInitialRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(TableInitialRunner.class);

    private static final String BASE_PACKAGE = "online.yangcloud.common.model";
    private static final String RESOURCE_PATTERN = "/*.class";

    @Resource
    private TableMapper tableMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 查询数据库中已存在的数据表
        List<String> tablesName = tableMapper.queryTablesName();

        // 利用反射获取指定包下所有的类
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(BASE_PACKAGE) + RESOURCE_PATTERN;
        org.springframework.core.io.Resource[] resources = resourcePatternResolver.getResources(pattern);
        MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        // 过滤所有扫描到的类，只对数据表类进行操作
        for (org.springframework.core.io.Resource resource : resources) {
            MetadataReader reader = readerFactory.getMetadataReader(resource);
            // 扫描到的class
            String classname = reader.getClassMetadata().getClassName();
            Class<?> clazz = Class.forName(classname);
            // 判断是否有指定主解
            FluentMybatis tableAnnotation = clazz.getAnnotation(FluentMybatis.class);
            if (ObjectUtil.isNotNull(tableAnnotation)) {
                // 如果检测到实体类对应的表已创建，那么就直接跳过，进行下一项
                if (tablesName.contains(tableAnnotation.table())) {
                    continue;
                }
                logger.info("Table [{}] has not been created yet and is being created for initialization", tableAnnotation.table());
                jdbcTemplate.execute(tableMapper.generateUserTable(clazz));
            }
        }
    }

}
