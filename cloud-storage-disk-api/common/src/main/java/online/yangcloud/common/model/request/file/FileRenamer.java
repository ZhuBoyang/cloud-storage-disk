package online.yangcloud.common.model.request.file;

import javax.validation.constraints.NotBlank;

/**
 * @author zhuboyang
 * @since 2023年06月26 10:49:14
 */
public class FileRenamer {

    /**
     * 文件 id
     */
    @NotBlank
    private String id;

    /**
     * 文件名
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
        return "FileRenamer["
                + " id=" + id + ","
                + " name=" + name
                + " ]";
    }
}
