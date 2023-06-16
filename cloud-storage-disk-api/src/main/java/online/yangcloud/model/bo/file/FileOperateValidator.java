package online.yangcloud.model.bo.file;

import online.yangcloud.model.FileMetadata;

import java.util.List;

/**
 * 复制、移动时的文件校验
 *
 * @author zhuboyang
 * @since 2023年01月16 10:49:43
 */
public class FileOperateValidator {

    /**
     * 目标目录
     */
    FileMetadata target;

    /**
     * 待操作文件和文件夹
     */
    List<FileMetadata> sources;

    public FileMetadata getTarget() {
        return target;
    }

    public FileOperateValidator setTarget(FileMetadata target) {
        this.target = target;
        return this;
    }

    public List<FileMetadata> getSources() {
        return sources;
    }

    public FileOperateValidator setSources(List<FileMetadata> sources) {
        this.sources = sources;
        return this;
    }

    @Override
    public String toString() {
        return "FileOperateValidator["
                + " target=" + target + ","
                + " sources=" + sources
                + " ]";
    }
}
