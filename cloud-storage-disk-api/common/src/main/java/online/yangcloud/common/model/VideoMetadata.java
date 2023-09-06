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
     * 总帧数
     **/
    private int totalFrames;

    /**
     * 帧率
     **/
    private double frameRate;

    /**
     * 时长
     **/
    private double duration;

    /**
     * 视频编码
     */
    private String videoCode;

    /**
     * 音频编码
     */
    private String audioCode;

    /**
     * 视频宽度
     */
    private int width;

    /**
     * 视频高度
     */
    private int height;

    /**
     * 音频通道
     */
    private int audioChannel;

    /**
     * 音频采样率
     */
    private Integer sampleRate;

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

    public int getTotalFrames() {
        return totalFrames;
    }

    public VideoMetadata setTotalFrames(int totalFrames) {
        this.totalFrames = totalFrames;
        return this;
    }

    public double getFrameRate() {
        return frameRate;
    }

    public VideoMetadata setFrameRate(double frameRate) {
        this.frameRate = frameRate;
        return this;
    }

    public double getDuration() {
        return duration;
    }

    public VideoMetadata setDuration(double duration) {
        this.duration = duration;
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

    public int getWidth() {
        return width;
    }

    public VideoMetadata setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public VideoMetadata setHeight(int height) {
        this.height = height;
        return this;
    }

    public int getAudioChannel() {
        return audioChannel;
    }

    public VideoMetadata setAudioChannel(int audioChannel) {
        this.audioChannel = audioChannel;
        return this;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public VideoMetadata setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
        return this;
    }

    @Override
    public String toString() {
        return "VideoMetadata["
                + " id=" + id + ","
                + " fileId=" + fileId + ","
                + " thumbnail=" + thumbnail + ","
                + " totalFrames=" + totalFrames + ","
                + " frameRate=" + frameRate + ","
                + " duration=" + duration + ","
                + " videoCode=" + videoCode + ","
                + " audioCode=" + audioCode + ","
                + " width=" + width + ","
                + " height=" + height + ","
                + " audioChannel=" + audioChannel + ","
                + " sampleRate=" + sampleRate
                + " ]"
                + " "
                + super.toString();
    }
}
