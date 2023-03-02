package online.yangcloud.model.ao.file;

import javax.validation.constraints.NotBlank;

/**
 * 文件播放请求
 *
 * @author zhuboyang
 * @since 2023年01月30 14:25:24
 */
public class FilePlayRequest {

    /**
     * 文件 id
     */
    @NotBlank
    private String fileId;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        return "FilePlayRequest["
                + " fileId=" + fileId
                + " ]";
    }
}
