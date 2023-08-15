package online.yangcloud.common.model.request.file;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zhuboyang
 * @since 2023年06月25 13:49:46
 */
public class DirLooker {

    /**
     * 父级目录 id
     */
    @NotBlank
    private String pid;

    /**
     * 目录 id
     */
    private String id;

    /**
     * 每次查询的数量
     */
    @NotNull
    private Integer size;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "DirLooker["
                + " pid=" + pid + ","
                + " id=" + id + ","
                + " size=" + size
                + " ]";
    }
}
