package online.yangcloud.model.ao.file;

import cn.hutool.core.text.CharSequenceUtil;

/**
 * 新建文件夹请求
 *
 * @author zhuboyang
 * @since 2023年01月05 16:37:12
 */
public class MkdirRequest {

    /**
     * 父级目录 id
     */
    private String pid = CharSequenceUtil.EMPTY;

    /**
     * 文件夹名称
     */
    private String fileName;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "MkdirRequest["
                + " pid=" + pid + ","
                + " fileName=" + fileName
                + " ]";
    }
}
