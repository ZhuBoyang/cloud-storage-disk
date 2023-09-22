package online.yangcloud.common.annotation;

import java.lang.annotation.*;

/**
 * @author zhuboyang
 * @since 2023年09月22 14:31:30
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface DatabaseIndex {

    /**
     * 是否唯一
     */
    boolean unique() default false;

}
