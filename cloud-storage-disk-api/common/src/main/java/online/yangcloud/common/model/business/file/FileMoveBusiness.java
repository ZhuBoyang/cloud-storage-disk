package online.yangcloud.common.model.business.file;

import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年03月28 08:59:41
 */
public class FileMoveBusiness {

    /**
     * 原文件父级文件的祖级 id 列表
     */
    private List<String> sourceAncestors;

    /**
     * 新的祖级 id 列表
     */
    private List<String> futureAncestors;

    /**
     * 初始化实体数据
     *
     * @param sourceAncestors 原文件的祖级 id 列表
     * @param futureAncestors 新文件的祖级 id 列表
     * @return result
     */
    public static FileMoveBusiness pack(List<String> sourceAncestors, List<String> futureAncestors) {
        return new FileMoveBusiness()
                .setSourceAncestors(sourceAncestors)
                .setFutureAncestors(futureAncestors);
    }

    public List<String> getSourceAncestors() {
        return sourceAncestors;
    }

    public FileMoveBusiness setSourceAncestors(List<String> sourceAncestors) {
        this.sourceAncestors = sourceAncestors;
        return this;
    }

    public List<String> getFutureAncestors() {
        return futureAncestors;
    }

    public FileMoveBusiness setFutureAncestors(List<String> futureAncestors) {
        this.futureAncestors = futureAncestors;
        return this;
    }

    @Override
    public String toString() {
        return "FileMoveBusiness["
                + " sourceAncestors=" + sourceAncestors + ","
                + " futureAncestors=" + futureAncestors
                + " ]";
    }
}
