package online.yangcloud.model.vo.file;

/**
 * 文件播放视图模型
 *
 * @author zhuboyang
 * @since 2023年01月31 11:06:07
 */
public class FilePlayView {

    /**
     * 文件临时存放路径
     */
    private String path;

    /**
     * 文件后缀
     */
    private String ext;

    /**
     * 文件附属属性（文件类型不同，属性不同）
     */
    private Object extend;

    /**
     * 包装数据
     *
     * @param path 文件临时存储路径
     * @param ext  文件后缀
     * @return result
     */
    public static FilePlayView packageData(String path, String ext) {
        return new FilePlayView()
                .setPath(path)
                .setExt(ext);
    }

    public String getPath() {
        return path;
    }

    public FilePlayView setPath(String path) {
        this.path = path;
        return this;
    }

    public String getExt() {
        return ext;
    }

    public FilePlayView setExt(String ext) {
        this.ext = ext;
        return this;
    }

    public Object getExtend() {
        return extend;
    }

    public FilePlayView setExtend(Object extend) {
        this.extend = extend;
        return this;
    }

    @Override
    public String toString() {
        return "FilePlayView["
                + " path=" + path + ","
                + " ext=" + ext + ","
                + " extend=" + extend
                + " ]";
    }
}
