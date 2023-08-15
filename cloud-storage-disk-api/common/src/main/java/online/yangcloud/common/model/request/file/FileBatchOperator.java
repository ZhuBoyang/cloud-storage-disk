package online.yangcloud.common.model.request.file;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年06月25 10:38:24
 */
public class FileBatchOperator {

    /**
     * 待操作文件 id 列表
     */
    @NotNull
    private List<String> sources;

    /**
     * 目标文件 id
     */
    @NotBlank
    private String target;

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "FileBatchOperator["
                + " sources=" + sources + ","
                + " target=" + target
                + " ]";
    }
}
