package online.yangcloud.common.runner;

import cn.hutool.core.util.StrUtil;
import online.yangcloud.common.annotation.TokenEntity;
import online.yangcloud.common.aspect.ServletLogAspect;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

/**
 * @author zhuboyang
 * @since 2023年08月17 10:59:10
 */
@Order(2)
@Component
public class TokenScannerRunner implements ApplicationRunner {

    private static final String RESOURCE_PATTERN = "/**/*.class";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 获取当前模块的全包名
        String name = this.getClass().getName();
        String basePackage = name.substring(0, name.lastIndexOf(StrUtil.DOT));
        basePackage = basePackage.substring(0, basePackage.lastIndexOf(StrUtil.DOT));

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(basePackage) + RESOURCE_PATTERN;
        Resource[] resources = resourcePatternResolver.getResources(pattern);
        // MetadataReader 的工厂类
        MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        for (Resource resource : resources) {
            // 用于读取类信息
            MetadataReader reader = readerFactory.getMetadataReader(resource);
            // 扫描到的class
            String classname = reader.getClassMetadata().getClassName();
            Class<?> clazz = Class.forName(classname);
            // 判断是否有指定主解
            TokenEntity entity = clazz.getAnnotation(TokenEntity.class);
            if (entity != null) {
                ServletLogAspect.TOKEN_CLASSPATH = classname;
            }
        }
    }

}
