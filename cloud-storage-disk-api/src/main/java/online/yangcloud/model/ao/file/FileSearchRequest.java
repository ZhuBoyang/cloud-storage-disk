package online.yangcloud.model.ao.file;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * 文件查询请求
 *
 * @author zhuboyang
 * @since 2023年01月03 11:11:21
 */
public class FileSearchRequest {

    /**
     * 父级目录 id
     */
    private String pid;

    /**
     * 起始点的文件 id
     */
    private String fileId = CharSequenceUtil.EMPTY;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 获取的数据量
     */
    private Integer size;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "FileSearchRequest["
                + " pid=" + pid + ","
                + " fileId=" + fileId + ","
                + " fileName=" + fileName + ","
                + " size=" + size
                + " ]";
    }
}
