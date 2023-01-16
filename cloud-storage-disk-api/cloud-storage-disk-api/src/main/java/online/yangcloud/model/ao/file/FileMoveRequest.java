package online.yangcloud.model.ao.file;

import java.util.List;

/**
 * 文件批量移动请求
 *
 * @author zhuboyang
 * @since 2023年01月12 16:00:54
 */
public class FileMoveRequest {

    /**
     * 待移动文件 id 列表
     */
    private List<String> sources;

    /**
     * 目标文件夹 id
     */
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
        return "FileMoveRequest["
                + " sources=" + sources + ","
                + " target=" + target
                + " ]";
    }
}
