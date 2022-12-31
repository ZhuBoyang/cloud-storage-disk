package online.yangcloud.enumration;

/**
 * @author zhuboyang
 * @since 2022/12/31 11:44
 */
public interface SuperEnum<T> {

    /**
     * 获取 code
     *
     * @return T
     */
    T getCode();

    /**
     * 获取值
     *
     * @return value
     */
    String getValue();

    /**
     * 对比是否是同一个code
     *
     * @param code code
     * @return 是否
     */
    default boolean is(Integer code) {
        return getCode().equals(code);
    }

    /**
     * 对比是否是同一个code
     *
     * @param code code
     * @return 是否
     */
    default boolean is(String code) {
        return getCode().toString().equals(code);
    }

}
