package online.yangcloud.common.model.request.user;

import javax.validation.constraints.NotBlank;

/**
 * @author zhuboyang
 * @since 2023年06月14 10:26:28
 */
public class BreadsLooker {

    /**
     * 当前所在目录的文件 id
     */
    @NotBlank
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BreadsLooker["
                + " id=" + id
                + " ]";
    }
}
