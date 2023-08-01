package online.yangcloud.model.request.file;

import javax.validation.constraints.NotBlank;

/**
 * @author zhuboyang
 * @since 2023年06月14 08:52:39
 */
public class FileMkdir {

    /**
     * 父级文件 id
     */
    @NotBlank
    private String id;

    /**
     * 文件夹名称
     */
    @NotBlank
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FileMkdir["
                + " id=" + id + ","
                + " name=" + name
                + " ]";
    }
}
