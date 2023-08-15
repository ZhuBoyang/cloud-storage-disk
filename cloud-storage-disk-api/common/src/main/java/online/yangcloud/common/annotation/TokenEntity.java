package online.yangcloud.common.annotation;

import java.lang.annotation.*;

/**
 * @author zhuboyang
 * @since 2023年08月15 10:23:30
 */
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface TokenEntity {
}
