package online.yangcloud.model.ao.file;

/**
 * 文件合并请求
 *
 * @author zhuboyang
 * @since 2023年01月06 14:09:46
 */
public class FileMergeRequest {

    /**
     * 文件唯一识别码
     */
    private String identifier;

    /**
     * 文件 hash
     */
    private String fileHash;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    @Override
    public String toString() {
        return "FileMergeRequest["
                + " identifier=" + identifier + ","
                + " fileHash=" + fileHash
                + " ]";
    }
}
