package online.yangcloud.model.vo.file;

/**
 * 文件列表导航面包屑
 *
 * @author zhuboyang
 * @since 2023年01月05 11:14:06
 */
public class FileBreadView {

    /**
     * 文件 id
     */
    private String id;

    /**
     * 文件名
     */
    private String name;

    /**
     * 封装视图数据
     *
     * @param id   文件 id
     * @param name 文件名
     * @return result
     */
    public static FileBreadView packageData(String id, String name) {
        return new FileBreadView().setId(id).setName(name);
    }

    public String getId() {
        return id;
    }

    public FileBreadView setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public FileBreadView setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "FileBreadView["
                + " id=" + id + ","
                + " name=" + name
                + " ]";
    }

}
