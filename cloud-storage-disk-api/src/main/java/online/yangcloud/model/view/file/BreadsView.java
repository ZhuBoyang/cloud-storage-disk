package online.yangcloud.model.view.file;

import cn.hutool.core.util.ObjectUtil;
import online.yangcloud.model.FileMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年06月14 10:32:11
 */
public class BreadsView {

    /**
     * 文件 id
     */
    private String id;

    /**
     * 文件夹名
     */
    private String name;

    /**
     * 顺序号
     */
    private Integer index;

    public static List<BreadsView> convert(List<FileMetadata> files) {
        if (ObjectUtil.isNull(files)) {
            return new ArrayList<>();
        }
        List<BreadsView> views = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            views.add(convert(files.get(i)).setIndex(i));
        }
        return views;
    }

    public static BreadsView convert(FileMetadata file) {
        return new BreadsView()
                .setId(file.getId())
                .setName(file.getName())
                .setIndex(0);
    }

    public String getId() {
        return id;
    }

    public BreadsView setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public BreadsView setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getIndex() {
        return index;
    }

    public BreadsView setIndex(Integer index) {
        this.index = index;
        return this;
    }

    @Override
    public String toString() {
        return "BreadsView["
                + " id=" + id + ","
                + " name=" + name + ","
                + " index=" + index
                + " ]";
    }
}
