package online.yangcloud.annotation;

import java.lang.annotation.*;

/**
 * @author zhuboyang
 * @since 2022/12/31 11:40
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
