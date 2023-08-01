package online.yangcloud.model.business.file;

import java.util.List;

/**
 * @author zhuboyang
 * @since 2023年03月25 12:16:03
 */
public class FileOperatorBusiness {

    /**
     * 新的父级文件 id
     */
    private String futureId;

    /**
     * 新的父级文件的祖级 id
     */
    private List<String> futureAncestors;

    /**
     * 初始化旧的父级文件 id 与旧的文件的祖级 id 的关系对象
     *
     * @param futureId        旧的父级文件 id
     * @param futureAncestors 旧的文件的祖级 id
     * @return 关系对象
     */
    public static FileOperatorBusiness pack(String futureId, List<String> futureAncestors) {
        return new FileOperatorBusiness()
                .setFutureId(futureId)
                .setFutureAncestors(futureAncestors);
    }

    public String getFutureId() {
        return futureId;
    }

    public FileOperatorBusiness setFutureId(String futureId) {
        this.futureId = futureId;
        return this;
    }

    public List<String> getFutureAncestors() {
        return futureAncestors;
    }

    public FileOperatorBusiness setFutureAncestors(List<String> futureAncestors) {
        this.futureAncestors = futureAncestors;
        return this;
    }

    @Override
    public String toString() {
        return "FileOperatorBusiness["
                + " futureId=" + futureId + ","
                + " historyAncestors=" + futureAncestors
                + " ]";
    }
}
