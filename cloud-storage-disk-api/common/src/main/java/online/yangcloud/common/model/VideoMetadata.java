package online.yangcloud.common.model;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import online.yangcloud.common.annotation.DatabaseColumn;
import online.yangcloud.common.enumration.DatabaseColumnTypeEnum;

/**
 * @author zhuboyang
 * @since 2023年09月04 15:35:56
 */
@FluentMybatis(table = "video_metadata", desc = "视频元数据")
public class VideoMetadata extends BaseParameter {

    /**
     * 主键
     */
    @TableId
    @DatabaseColumn(name = "id", type = DatabaseColumnTypeEnum.VARCHAR, length = 32, canNull = false)
    private String id;

    /**
     * 文件 id
     */
    @DatabaseColumn(name = "file_id", type = DatabaseColumnTypeEnum.VARCHAR, length = 32, canNull = false, comment = "文件 id")
    private String fileId;

    /**
     * 缩略图
     */
    @DatabaseColumn(name = "thumbnail", type = DatabaseColumnTypeEnum.VARCHAR, length = 128, canNull = false, comment = "视频缩略图地址")
    private String thumbnail;

    /**
     * 总时长
     */
    @DatabaseColumn(name = "duration", type = DatabaseColumnTypeEnum.DOUBLE, canNull = false, comment = "总时长")
    private Double duration;

    /**
     * 比特率
     */
    @DatabaseColumn(name = "bitrate", type = DatabaseColumnTypeEnum.VARCHAR, length = 20, canNull = false, comment = "比特率")
    private String bitrate;

    /**
     * 分辨率-宽
     */
    @DatabaseColumn(name = "width", type = DatabaseColumnTypeEnum.SMALLINT, canNull = false, comment = "分辨率宽度")
    private Integer width;

    /**
     * 分辨率-高
     */
    @DatabaseColumn(name = "height", type = DatabaseColumnTypeEnum.SMALLINT, canNull = false, comment = "分辨率高度")
    private Integer height;

    /**
     * 视频编码
     */
    @DatabaseColumn(name = "video_code", type = DatabaseColumnTypeEnum.VARCHAR, length = 10, canNull = false, comment = "视频编码")
    private String videoCode;

    /**
     * 音频编码
     */
    @DatabaseColumn(name = "audio_code", type = DatabaseColumnTypeEnum.VARCHAR, length = 10, canNull = false, comment = "音频编码")
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
