package online.yangcloud.model.ao.file;

/**
 * 批量删除请求体
 *
 * @author zhuboyang
 * @since 2023年01月10 17:25:38
 */
public class BatchDeleteRequest {

    /**
     * 要删除的文件 id 列表
     */
    private String fileString;

    public String getFileString() {
        return fileString;
    }

    public void setFileString(String fileString) {
        this.fileString = fileString;
    }

    @Override
    public String toString() {
        return "BatchDeleteRequest["
                + " fileString=" + fileString
                + " ]";
    }
}
