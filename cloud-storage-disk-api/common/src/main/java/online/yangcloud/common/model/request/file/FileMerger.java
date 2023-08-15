package online.yangcloud.common.model.request.file;

import javax.validation.constraints.NotBlank;

/**
 * @author zhuboyang
 * @since 2023年03月25 21:17:26
 */
public class FileMerger {

    /**
     * 文件唯一标识
     */
    @NotBlank
    private String identifier;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "FileMerger["
                + " identifier=" + identifier
                + " ]";
    }
}
