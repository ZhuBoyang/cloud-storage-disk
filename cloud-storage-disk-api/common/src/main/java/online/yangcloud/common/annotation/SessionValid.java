package online.yangcloud.common.annotation;

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

    /**
     * 未检测到用户类的报错提示
     *
     * @return 报错信息内容
     */
    String errorMsg() default "未检测到用户类，请在用户类上修饰{TokenEntity}注解";

}
