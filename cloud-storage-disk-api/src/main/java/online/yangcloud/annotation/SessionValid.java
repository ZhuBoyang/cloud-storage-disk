package online.yangcloud.annotation;

import java.lang.annotation.*;

/**
 * @author zhuboyang
 * @since 2022年03月13 13:46:57
 */
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface SessionValid {

    /**
     * 是否需要校验
     *
     * @return result
     */
    boolean needValid() default true;

}
