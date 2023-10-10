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

    /**
     * 获取视频支持的后缀格式
     *
     * @return 格式列表
     */
    public List<String> acquireVideoSupports() {
        if (StrUtil.isBlank(video)) {
            return Collections.emptyList();
        }
        List<String> ext = StrUtil.split(video, StrUtil.COMMA);
        return ext.stream().map(o -> StrUtil.DOT + o).collect(Collectors.toList());
    }

    /**
     * 获取音频支持的后缀格式
     *
     * @return 格式列表
     */
    public List<String> acquireAudioSupports() {
        if (StrUtil.isBlank(audio)) {
            return Collections.emptyList();
        }
        List<String> ext = StrUtil.split(audio, StrUtil.COMMA);
        return ext.stream().map(o -> StrUtil.DOT + o).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "FileExtTypeProperty["
                + " video=" + video + ","
                + " audio=" + audio
                + " ]";
    }
}
