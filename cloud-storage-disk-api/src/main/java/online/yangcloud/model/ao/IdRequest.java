package online.yangcloud.model.ao;

import javax.validation.constraints.NotBlank;

/**
 * @author zhuboyang
 * @since 2023年03月16 15:25:22
 */
public class IdRequest {

    /**
     * 数据 id
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
        return "IdRequest["
                + " id=" + id
                + " ]";
    }
}
