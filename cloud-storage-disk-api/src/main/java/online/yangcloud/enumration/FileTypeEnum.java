package online.yangcloud.enumration;

/**
 * 文件类型枚举
 *
 * @author zhuboyang
 * @since 2023年01月03 09:35:41
 */
public enum FileTypeEnum implements SuperEnum<Integer> {

    /**
     * 文件
     */
    FILE(0, "文件"),

    /**
     * 文件夹
     */
    DIR(1, "文件夹"),
    ;

    private final Integer code;
    private final String value;

    FileTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getValue() {
        return value;
    }

}
