package online.yangcloud.common.annotation;

import java.lang.annotation.*;

/**
 * 防止重复请求
 *
 * @author zhuboyang
 * @since 2023年08月16 11:08:32
 */
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatRequest {
}
