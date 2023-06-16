package online.yangcloud.model.vo.file;

import cn.hutool.core.util.ObjectUtil;
import online.yangcloud.model.FileMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return files.stream().map(BreadsView::convert).collect(Collectors.toList());
    }

    public static BreadsView convert(FileMetadata file) {
        return new BreadsView()
                .setId(file.getId())
                .setName(file.getName());
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
