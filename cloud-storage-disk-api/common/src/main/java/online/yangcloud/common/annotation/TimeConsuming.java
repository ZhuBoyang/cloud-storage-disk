package online.yangcloud.common.annotation;

import java.lang.annotation.*;

/**
 * @author zhuboyang
 * @since 2023年08月15 08:51:31
 */
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface TimeConsuming {
}
