package online.yangcloud.model.request.file;

import javax.validation.constraints.NotBlank;

/**
 * @author zhuboyang
 * @since 2023年07月31 16:33:11
 */
public class FileSizeChecker {

    /**
     * 文件大小
     */
    @NotBlank
    private String sizes;

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    @Override
    public String toString() {
        return "FileSizeChecker["
                + " sizes=" + sizes
                + " ]";
    }
}
