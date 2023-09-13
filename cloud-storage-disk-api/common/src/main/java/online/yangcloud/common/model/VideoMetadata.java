package online.yangcloud.common.model;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;

/**
 * @author zhuboyang
 * @since 2023年09月04 15:35:56
 */
@FluentMybatis(table = "video_metadata")
public class VideoMetadata extends BaseParameter {

    /**
     * 主键
     */
    @TableId
    private String id;

    /**
     * 文件 id
     */
    private String fileId;

    /**
     * 缩略图
     */
    private String thumbnail;

    /**
     * 总时长
     */
    private Double duration;

    /**
     * 比特率
     */
    private String bitrate;

    /**
     * 分辨率-宽
     */
    private Integer width;

    /**
     * 分辨率-高
     */
    private Integer height;

    /**
     * 视频编码
     */
    private String videoCode;

    /**
     * 音频编码
     */
    private String audioCode;

    public static VideoMetadata initial() {
        return new VideoMetadata();
    }

    public String getId() {
        return id;
    }

    public VideoMetadata setId(String id) {
        this.id = id;
        return this;
    }

    public String getFileId() {
        return fileId;
    }

    public VideoMetadata setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public VideoMetadata setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public Double getDuration() {
        return duration;
    }

    public VideoMetadata setDuration(Double duration) {
        this.duration = duration;
        return this;
    }

    public String getBitrate() {
        return bitrate;
    }

    public VideoMetadata setBitrate(String bitrate) {
        this.bitrate = bitrate;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    public VideoMetadata setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    public VideoMetadata setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public String getVideoCode() {
        return videoCode;
    }

    public VideoMetadata setVideoCode(String videoCode) {
        this.videoCode = videoCode;
        return this;
    }

    public String getAudioCode() {
        return audioCode;
    }

    public VideoMetadata setAudioCode(String audioCode) {
        this.audioCode = audioCode;
        return this;
    }

    @Override
    public String toString() {
        return "VideoMetadata["
                + " id=" + id + ","
                + " fileId=" + fileId + ","
                + " thumbnail=" + thumbnail + ","
                + " duration=" + duration + ","
                + " bitrate=" + bitrate + ","
                + " width=" + width + ","
                + " height=" + height + ","
                + " videoCode=" + videoCode + ","
                + " audioCode=" + audioCode
                + " ]"
                + " "
                + super.toString();
    }
}
