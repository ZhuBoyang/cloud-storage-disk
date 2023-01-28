package online.yangcloud.model.bo;

import online.yangcloud.model.po.FileMetadata;

import java.util.List;

/**
 * 复制、移动时的文件校验
 *
 * @author zhuboyang
 * @since 2023年01月16 10:49:43
 */
public class FileOperationValidate {

    /**
     * 目标目录
     */
    FileMetadata targetFile;

    /**
     * 待操作文件和文件夹
     */
    List<FileMetadata> sourceFiles;

    public FileMetadata getTargetFile() {
        return targetFile;
    }

    public FileOperationValidate setTargetFile(FileMetadata targetFile) {
        this.targetFile = targetFile;
        return this;
    }

    public List<FileMetadata> getSourceFiles() {
        return sourceFiles;
    }

    public FileOperationValidate setSourceFiles(List<FileMetadata> sourceFiles) {
        this.sourceFiles = sourceFiles;
        return this;
    }

    @Override
    public String toString() {
        return "FileOperationValidate["
                + " targetFile=" + targetFile + ","
                + " sourceFiles=" + sourceFiles
                + " ]";
    }
}
