package online.yangcloud.model.ao.file;

import online.yangcloud.enumration.YesOrNoEnum;
import online.yangcloud.model.ao.PagerParameter;

/**
 * @author zhuboyang
 * @since 2023年06月15 11:26:19
 */
public class FileSearcher extends PagerParameter {

    /**
     * 父级目录 id
     */
    private String pid;

    /**
     * 文件名
     */
    private String name;

    /**
     * 是否只查询文件夹
     */
    private Integer isOnlyDir = YesOrNoEnum.NO.code();

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsOnlyDir() {
        return isOnlyDir;
    }

    public void setIsOnlyDir(Integer isOnlyDir) {
        this.isOnlyDir = isOnlyDir;
    }

    @Override
    public String toString() {
        return "FileSearcher["
                + " pid=" + pid + ","
                + " name=" + name + ","
                + " isOnlyDir=" + isOnlyDir
                + " ]"
                + " "
                + super.toString();
    }
}
