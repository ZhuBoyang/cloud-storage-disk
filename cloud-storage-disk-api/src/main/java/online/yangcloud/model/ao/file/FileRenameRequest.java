package online.yangcloud.model.ao.file;

import javax.validation.constraints.NotBlank;

/**
 * 文件重命名请求
 *
 * @author zhuboyang
 * @since 2023年01月28 13:03:52
 */
public class FileRenameRequest {

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
        return "FileRenameRequest["
                + " id=" + id + ","
                + " name=" + name
                + " ]";
    }
}
