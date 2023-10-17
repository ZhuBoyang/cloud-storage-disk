package online.yangcloud.common.properties;

import cn.hutool.core.util.StrUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhuboyang
 * @since 2023年09月23 15:33:40
 */
@ConfigurationProperties(prefix = "media")
@Component
public class FileExtTypeProperty {

    /**
     * 视频后缀
     */
    private String video;

    /**
     * 音频后缀
     */
    private String audio;

    /**
     * word 后缀
     */
    private String word;

    /**
     * ppt 后缀
     */
    private String ppt;

    /**
     * excel 后缀
     */
    private String excel;

    /**
     * 获取视频支持的后缀格式
     *
     * @return 格式列表
     */
    public List<String> acquireVideoSupports() {
        return StrUtil.isBlank(video) ? Collections.emptyList() : addDotPrefix(video);
    }

    /**
     * 获取音频支持的后缀格式
     *
     * @return 格式列表
     */
    public List<String> acquireAudioSupports() {
        return StrUtil.isBlank(audio) ? Collections.emptyList() : addDotPrefix(audio);
    }

    /**
     * 获取 word 支持的后缀格式
     *
     * @return 格式列表
     */
    public List<String> acquireWordSupports() {
        return StrUtil.isBlank(word) ? Collections.emptyList() : addDotPrefix(word);
    }

    /**
     * 获取 ppt 支持的后缀格式
     *
     * @return 格式列表
     */
    public List<String> acquirePptSupports() {
        return StrUtil.isBlank(ppt) ? Collections.emptyList() : addDotPrefix(ppt);
    }

    /**
     * 获取 excel 支持的后缀格式
     *
     * @return 格式列表
     */
    public List<String> acquireExcelSupports() {
        return StrUtil.isBlank(excel) ? Collections.emptyList() : addDotPrefix(excel);
    }

    /**
     * 获取 office 支持的后缀格式
     *
     * @return 格式列表
     */
    public List<String> acquireOfficeSupports() {
        List<String> supports = acquireWordSupports();
        supports.addAll(acquirePptSupports());
        supports.addAll(acquireExcelSupports());
        return supports;
    }

    private List<String> addDotPrefix(String ext) {
        return StrUtil.split(ext, StrUtil.COMMA).stream().map(o -> StrUtil.DOT + o).collect(Collectors.toList());
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPpt() {
        return ppt;
    }

    public void setPpt(String ppt) {
        this.ppt = ppt;
    }

    public String getExcel() {
        return excel;
    }

    public void setExcel(String excel) {
        this.excel = excel;
    }

    @Override
    public String toString() {
        return "FileExtTypeProperty["
                + " video=" + video + ","
                + " audio=" + audio + ","
                + " word=" + word + ","
                + " ppt=" + ppt + ","
                + " excel=" + excel
                + " ]";
    }
}
