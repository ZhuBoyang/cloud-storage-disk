package online.yangcloud.common.annotation;

import online.yangcloud.common.enumration.DatabaseColumnTypeEnum;

import java.lang.annotation.*;

/**
 * @author zhuboyang
 * @since 2023年08月15 10:23:30
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface DatabaseColumn {

    /**
     * 表字段名
     */
    String name();

    /**
     * 字段类型
     */
    DatabaseColumnTypeEnum type();

    /**
     * 字段长度
     */
    int length() default 1;

    /**
     * 是否可为空
     */
    boolean canNull() default true;

    /**
     * 字段默认值
     */
    String defaultValue() default "";

    /**
     * 字段注释
     */
    String comment() default "";

}
